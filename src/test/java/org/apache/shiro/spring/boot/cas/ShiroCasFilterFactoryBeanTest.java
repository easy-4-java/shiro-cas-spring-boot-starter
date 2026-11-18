package org.apache.shiro.spring.boot.cas;

import java.util.Collections;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.support.StaticApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ShiroCasFilterFactoryBeanTest {

    @Test
    void discoversOnlyCompatibleServletFiltersAndPreservesExplicitOverrides() {
        Filter accessControl = mock(AccessControlFilter.class);
        Filter logout = new LogoutFilter();
        Filter explicitOverride = mock(Filter.class);
        try (StaticApplicationContext context = new StaticApplicationContext()) {
            context.getBeanFactory().registerSingleton("casAccess", new FilterRegistrationBean<>(accessControl));
            context.getBeanFactory().registerSingleton("casLogout", new FilterRegistrationBean<>(logout));
            context.getBeanFactory().registerSingleton("unrelated", new FilterRegistrationBean<>(mock(Filter.class)));
            context.refresh();

            ShiroCasFilterFactoryBean factory = new ShiroCasFilterFactoryBean();
            factory.setApplicationContext(context);

            assertThat(factory.getFilters()).containsOnlyKeys("casAccess", "casLogout");
            assertThat(factory.getFilters().get("casAccess")).isSameAs(accessControl);

            factory.setFilters(Collections.singletonMap("casAccess", explicitOverride));
            Map<String, Filter> filters = factory.getFilters();
            assertThat(filters.get("casAccess")).isSameAs(explicitOverride);
            assertThat(filters.get("casLogout")).isSameAs(logout);
        }
    }
}
