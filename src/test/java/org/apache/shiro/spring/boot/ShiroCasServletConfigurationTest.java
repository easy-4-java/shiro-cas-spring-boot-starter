package org.apache.shiro.spring.boot;

import java.util.stream.Stream;

import org.apache.shiro.event.EventBus;
import org.apache.shiro.event.support.DefaultEventBus;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.spring.boot.cas.CasSubjectFactory;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.jasig.cas.client.Protocol;
import org.jasig.cas.client.validation.AbstractTicketValidationFilter;
import org.jasig.cas.client.validation.Cas10TicketValidationFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.jasig.cas.client.validation.Saml11TicketValidationFilter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import static org.assertj.core.api.Assertions.assertThat;

class ShiroCasServletConfigurationTest {

    @ParameterizedTest
    @MethodSource("protocolFilters")
    void casAndShiroBizShareOneServletRegistration(Protocol protocol,
                                                   Class<? extends AbstractTicketValidationFilter> validator) {
        new WebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(ShiroBizAutoConfiguration.class,
                        ShiroBizWebAutoConfiguration.class, ShiroBizWebFilterConfiguration.class,
                        ShiroCasWebAutoConfiguration.class, ShiroCasWebFilterConfiguration.class))
                .withBean(EventBus.class, DefaultEventBus::new)
                .withPropertyValues("shiro.enabled=true", "shiro.cas.enabled=true", "shiro.cas.protocol=" + protocol.name(),
                        "shiro.static-security-manager-enabled=false",
                        "shiro.cas.cas-server-login-url=https://sso.example.test/login",
                        "shiro.cas.cas-server-logout-url=https://sso.example.test/logout",
                        "shiro.cas.cas-server-url-prefix=https://sso.example.test",
                        "shiro.cas.server-name=https://service.example.test",
                        "shiro.cas.server-callback-url=/callback",
                        "shiro.cas.service=https://service.example.test/callback",
                        "shiro.cas.ticket-validation-filter-url-patterns=/callback",
                        "shiro.filter-chain-definition-map.[/callback]=cas")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBean(SubjectFactory.class)).isInstanceOf(CasSubjectFactory.class);
                    FilterRegistrationBean<?> outer = context.getBean("filterShiroFilterRegistrationBean",
                            FilterRegistrationBean.class);
                    assertThat(outer.isEnabled()).isTrue();
                    ShiroFilterFactoryBean factory = context.getBean("&shiroFilterFactoryBean", ShiroFilterFactoryBean.class);
                    assertThat(factory.getFilterChainDefinitionMap()).containsEntry("/callback", "cas");
                    assertThat(factory.getFilters()).containsKeys("cas", "casLogout");
                    FilterRegistrationBean<?> tickets = context.getBean("ticketValidationFilter", FilterRegistrationBean.class);
                    assertThat(tickets.getFilter()).isExactlyInstanceOf(validator);
                    assertThat(tickets.getUrlPatterns()).containsExactly("/callback");
                    assertThat(context.getBean("cas", FilterRegistrationBean.class).isEnabled()).isFalse();
                });
    }
    static Stream<Arguments> protocolFilters() {
        return Stream.of(Arguments.of(Protocol.CAS1, Cas10TicketValidationFilter.class),
                Arguments.of(Protocol.CAS2, Cas20ProxyReceivingTicketValidationFilter.class),
                Arguments.of(Protocol.CAS3, Cas30ProxyReceivingTicketValidationFilter.class),
                Arguments.of(Protocol.SAML11, Saml11TicketValidationFilter.class));
    }

}
