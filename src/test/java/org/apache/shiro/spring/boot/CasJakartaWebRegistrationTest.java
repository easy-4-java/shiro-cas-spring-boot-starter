package org.apache.shiro.spring.boot;

import jakarta.servlet.Filter;
import org.apereo.cas.client.session.SingleSignOutHttpSessionListener;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.spring.boot.cas.CasSubjectFactory;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class CasJakartaWebRegistrationTest {

    private final WebApplicationContextRunner runner = new WebApplicationContextRunner()
            .withUserConfiguration(Application.class)
            .withPropertyValues(
                    "shiro.cache.type=memory",
                    "shiro.enabled=true",
                    "shiro.cas.cas-server-url-prefix=https://cas.example.invalid/cas",
                    "shiro.cas.cas-server-login-url=https://cas.example.invalid/cas/login",
                    "shiro.cas.cas-server-logout-url=https://cas.example.invalid/cas/logout",
                    "shiro.cas.server-name=https://app.example.invalid");

    @ParameterizedTest
    @CsvSource({
            "CAS1,Cas10TicketValidationFilter,AuthenticationFilter",
            "CAS2,Cas20ProxyReceivingTicketValidationFilter,AuthenticationFilter",
            "CAS3,Cas30ProxyReceivingTicketValidationFilter,AuthenticationFilter",
            "SAML11,Saml11TicketValidationFilter,Saml11AuthenticationFilter"
    })
    void registersTheJakartaFiltersAndListener(String protocol, String validatorType, String authenticationType) {
        runner.withPropertyValues("shiro.cas.enabled=true", "shiro.cas.protocol=" + protocol)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBean(SubjectFactory.class)).isInstanceOf(CasSubjectFactory.class);
                    var validator = context.getBean("ticketValidationFilter", FilterRegistrationBean.class);
                    assertThat(validator.isEnabled()).isTrue();
                    assertThat(validator.getFilter().getClass().getSimpleName()).isEqualTo(validatorType);
                    assertThat(validator.getInitParameters()).containsEntry(
                            "casServerUrlPrefix", "https://cas.example.invalid/cas");
                    var authentication = context.getBean("authenticationFilter", FilterRegistrationBean.class);
                    assertThat(authentication.getFilter().getClass().getSimpleName()).isEqualTo(authenticationType);
                    assertThat(context.getBean("singleSignOutHttpSessionListener", ServletListenerRegistrationBean.class)
                            .getListener()).isInstanceOf(SingleSignOutHttpSessionListener.class);
                });
    }

    @Test
    void redirectsToCasOnlyWhenTheShiroLogoutPathIsSelected() {
        runner.withPropertyValues(
                        "shiro.cas.enabled=true",
                        "shiro.cas.protocol=CAS3",
                        "shiro.filter-chain-definition-map.[/sign-out]=casLogout")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBean(DefaultWebSecurityManager.class).getSubjectFactory())
                            .isSameAs(context.getBean(CasSubjectFactory.class));
                    var logout = context.getBean("casLogout", FilterRegistrationBean.class);
                    assertThat(logout.isEnabled()).isFalse();
                    Filter dispatch = context.getBean("filterShiroFilterRegistrationBean", FilterRegistrationBean.class)
                            .getFilter();

                    var request = new MockHttpServletRequest("GET", "/sign-out");
                    request.setServletPath("/sign-out");
                    var response = new MockHttpServletResponse();
                    dispatch.doFilter(request, response, new MockFilterChain());

                    assertThat(response.getStatus()).isEqualTo(302);
                    assertThat(response.getRedirectedUrl()).startsWith("https://cas.example.invalid/cas/logout");
                });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    static class Application {
    }
}
