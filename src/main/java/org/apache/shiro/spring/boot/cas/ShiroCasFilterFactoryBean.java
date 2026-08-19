package org.apache.shiro.spring.boot.cas;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;

import org.apache.shiro.biz.spring.ShiroFilterProxyFactoryBean;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.springframework.beans.BeansException;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ObjectUtils;


/**
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class ShiroCasFilterFactoryBean extends ShiroFilterProxyFactoryBean implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	/**
	 * Returns the application context.
	 *
	 * @return the application context
	 */
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * Constructs a new shiro cas filter factory bean instance.
	 *
	 */
	public ShiroCasFilterFactoryBean() {
	}

	/**
	 * Determines whether supports.
	 *
	 * @param filter the filter
	 * @return the result
	 */
	protected boolean supports(Filter filter) {
		return filter instanceof AccessControlFilter ||  filter instanceof LogoutFilter;
	}

	// 过滤器链：实现对路径规则的拦截过滤
	/**
	 * Returns the filters.
	 *
	 * @return the filters
	 */
	@Override
	public Map<String, Filter> getFilters() {

		Map<String, Filter> filters = new LinkedHashMap<String, Filter>();

		Map<String, FilterRegistrationBean> beansOfType = getApplicationContext()
				.getBeansOfType(FilterRegistrationBean.class);
		if (!ObjectUtils.isEmpty(beansOfType)) {
			Iterator<Entry<String, FilterRegistrationBean>> ite = beansOfType.entrySet().iterator();
			while (ite.hasNext()) {
				Entry<String, FilterRegistrationBean> entry = ite.next();
				Object delegate = entry.getValue().getFilter();
				// Unwrap JakartaFilterAdapter if present
				Filter filter = unwrapFilter(delegate);
				if (filter != null && this.supports(filter)) {
					filters.put(entry.getKey(), filter);
				}
			}
		}

		filters.putAll(super.getFilters());

		return filters;

	}

	/**
	 * Unwrap a filter from a JakartaFilterAdapter if needed.
	 */
	private Filter unwrapFilter(Object delegate) {
		if (delegate instanceof Filter) {
			return (Filter) delegate;
		}
		// Handle JakartaFilterAdapter wrapping javax.servlet.Filter
		try {
			java.lang.reflect.Method getDelegate = delegate.getClass().getMethod("getDelegate");
			Object inner = getDelegate.invoke(delegate);
			if (inner instanceof Filter) {
				return (Filter) inner;
			}
		} catch (Exception ignored) {
		}
		return null;
	}

	/**
	 * Sets the application context.
	 *
	 * @param applicationContext the application context
	 * @throws BeansException if an error occurs
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
