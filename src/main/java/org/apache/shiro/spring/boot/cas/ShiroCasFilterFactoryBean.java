package org.apache.shiro.spring.boot.cas;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.biz.spring.ShiroFilterProxyFactoryBean;
import org.apache.shiro.spring.boot.biz.ShiroBizFilterFactoryBean;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.springframework.beans.BeansException;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
public class ShiroCasFilterFactoryBean extends ShiroFilterProxyFactoryBean implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public ShiroCasFilterFactoryBean() {
	}
	
	protected boolean supports(Filter filter) {
		return filter instanceof AccessControlFilter ||  filter instanceof LogoutFilter;
	}
	
	// 过滤器链：实现对路径规则的拦截过滤
	@Override
	public Map<String, Filter> getFilters() {

		Map<String, Filter> filters = new LinkedHashMap<String, Filter>();

        ApplicationContext context = getApplicationContext();
        for (String name : context.getBeanNamesForType(FilterRegistrationBean.class, true, false)) {
            if (ShiroBizFilterFactoryBean.FILTER_REGISTRATION_BEAN_NAME.equals(name)) {
                continue;
            }
            Filter filter = context.getBean(name, FilterRegistrationBean.class).getFilter();
            if (supports(filter)) {
                filters.put(name, filter);
            }
        }

		filters.putAll(super.getFilters());

		return filters;

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
