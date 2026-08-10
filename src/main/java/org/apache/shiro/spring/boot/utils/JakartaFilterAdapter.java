package org.apache.shiro.spring.boot.utils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Adapter that bridges a {@code javax.servlet.Filter} to a {@code jakarta.servlet.Filter}.
 * <p>Shiro's filter classes implement {@code javax.servlet.Filter} while Spring Boot 4.x
 * requires {@code jakarta.servlet.Filter} for filter registration. This adapter uses
 * reflection to delegate calls, avoiding direct type incompatibility.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class JakartaFilterAdapter implements Filter {

    private final Object delegate;
    private final boolean isJakarta;

    /**
     * Creates an adapter wrapping the given filter instance.
     *
     * @param delegate the filter to wrap (javax.servlet.Filter or jakarta.servlet.Filter)
     */
    public JakartaFilterAdapter(Object delegate) {
        this.delegate = delegate;
        this.isJakarta = implementsInterface(delegate, "jakarta.servlet.Filter");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            Method initMethod = findMethod("init", new String[]{"javax.servlet.FilterConfig", "jakarta.servlet.FilterConfig"});
            if (initMethod != null) {
                Object configArg = isJakarta ? filterConfig : adaptToJavax(filterConfig, "javax.servlet.FilterConfig");
                initMethod.invoke(delegate, configArg);
            }
        } catch (Exception e) {
            throw new ServletException("Failed to init delegate filter", e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            String chainClassName = isJakarta ? "jakarta.servlet.FilterChain" : "javax.servlet.FilterChain";
            Method doFilterMethod = delegate.getClass().getMethod("doFilter",
                    ServletRequest.class, ServletResponse.class, Class.forName(chainClassName));
            Object chainArg = isJakarta ? chain : createJavaxFilterChainProxy(chain);
            doFilterMethod.invoke(delegate, request, response, chainArg);
        } catch (Exception e) {
            throw new ServletException("Failed to invoke doFilter on delegate", e);
        }
    }

    @Override
    public void destroy() {
        try {
            Method destroyMethod = delegate.getClass().getMethod("destroy");
            destroyMethod.invoke(delegate);
        } catch (Exception e) {
            // ignore destroy errors
        }
    }

    /**
     * Returns the underlying delegate filter.
     *
     * @return the delegate object
     */
    public Object getDelegate() {
        return delegate;
    }

    private boolean implementsInterface(Object obj, String interfaceName) {
        try {
            Class<?> iface = Class.forName(interfaceName);
            return iface.isInstance(obj);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private Method findMethod(String name, String[] paramClassNames) {
        for (String paramClassName : paramClassNames) {
            try {
                Class<?> paramClass = Class.forName(paramClassName);
                return delegate.getClass().getMethod(name, paramClass);
            } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            }
        }
        return null;
    }

    private Object adaptToJavax(Object jakartaObj, String javaxClassName) throws Exception {
        Class<?> javaxClass = Class.forName(javaxClassName);
        return Proxy.newProxyInstance(
                javaxClass.getClassLoader(),
                new Class<?>[]{javaxClass},
                new JavaxAdapterInvocationHandler(jakartaObj)
        );
    }

    private Object createJavaxFilterChainProxy(FilterChain jakartaChain) throws Exception {
        Class<?> javaxFilterChainClass = Class.forName("javax.servlet.FilterChain");
        return Proxy.newProxyInstance(
                javaxFilterChainClass.getClassLoader(),
                new Class<?>[]{javaxFilterChainClass},
                (proxy, method, args) -> {
                    if ("doFilter".equals(method.getName()) && args.length == 2) {
                        jakartaChain.doFilter((ServletRequest) args[0], (ServletResponse) args[1]);
                        return null;
                    }
                    return method.invoke(jakartaChain, args);
                }
        );
    }

    /**
     * InvocationHandler that adapts jakarta method calls by finding and invoking
     * the equivalent method on the target object.
     */
    private static class JavaxAdapterInvocationHandler implements InvocationHandler {
        private final Object target;

        JavaxAdapterInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
            return targetMethod.invoke(target, args);
        }
    }
}
