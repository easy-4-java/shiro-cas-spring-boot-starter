package org.apache.shiro.spring.boot.cas;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.servlet.Filter;

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
				var filter = entry.getValue().getFilter();
				if (this.supports(filter)) {
					filters.put(entry.getKey(), filter);
				}
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
