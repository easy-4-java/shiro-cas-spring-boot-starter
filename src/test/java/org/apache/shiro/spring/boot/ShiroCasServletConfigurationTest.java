package org.apache.shiro.spring.boot;

import java.util.stream.Stream;

import org.apache.shiro.event.EventBus;
import org.apache.shiro.event.support.DefaultEventBus;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.spring.boot.cas.CasSubjectFactory;
import org.apache.shiro.spring.boot.cas.filter.CasLogoutFilter;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.jasig.cas.client.Protocol;
import org.jasig.cas.client.configuration.ConfigurationKeys;
import org.jasig.cas.client.validation.AbstractTicketValidationFilter;
import org.jasig.cas.client.validation.Cas10TicketValidationFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.jasig.cas.client.validation.Saml11TicketValidationFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import static org.assertj.core.api.Assertions.assertThat;

class ShiroCasServletConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(ShiroBizAutoConfiguration.class,
                        ShiroBizWebAutoConfiguration.class, ShiroBizWebFilterConfiguration.class,
                        ShiroCasWebAutoConfiguration.class, ShiroCasWebFilterConfiguration.class))
                .withBean(EventBus.class, DefaultEventBus::new)
                .withPropertyValues("shiro.enabled=true", "shiro.cas.enabled=true",
                        "shiro.static-security-manager-enabled=false",
                        "shiro.cas.cas-server-login-url=https://sso.example.test/login",
                        "shiro.cas.cas-server-logout-url=https://sso.example.test/logout",
                        "shiro.cas.cas-server-url-prefix=https://sso.example.test",
                        "shiro.cas.server-name=https://service.example.test",
                        "shiro.cas.server-callback-url=/callback",
                        "shiro.cas.service=https://service.example.test/callback",
                        "shiro.cas.ticket-validation-filter-url-patterns=/callback",
                        "shiro.filter-chain-definition-map.[/callback]=cas");

    @ParameterizedTest
    @MethodSource("protocolFilters")
    void casAndShiroBizShareOneServletRegistration(Protocol protocol,
                                                   Class<? extends AbstractTicketValidationFilter> validator) {
        contextRunner.withPropertyValues("shiro.cas.protocol=" + protocol.name())
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
    @Test
    void appliesCas2ServletOptionsAndKeepsRoamingLogoutLocal() {
        contextRunner.withPropertyValues("shiro.cas.protocol=CAS2", "shiro.cas.ca-mode=ROAM",
                        "shiro.login-url=/local-login", "shiro.cas.allowed-proxy-chains=https://service.example.test/proxy",
                        "shiro.cas.authentication-redirect-strategy-class=org.jasig.cas.client.authentication.DefaultAuthenticationRedirectStrategy",
                        "shiro.cas.gateway-storage-class=org.jasig.cas.client.authentication.DefaultGatewayResolverImpl",
                        "shiro.cas.ignore-pattern=/assets/.*", "shiro.cas.ignore-url-pattern-type=REGEX",
                        "shiro.cas.logout-parameter-name=logoutRequest", "shiro.cas.proxy-receptor-url=/proxy",
                        "shiro.cas.proxy-callback-url=https://service.example.test/proxy",
                        "shiro.cas.proxy-granting-ticket-storage-class=org.jasig.cas.client.proxy.ProxyGrantingTicketStorageImpl",
                        "shiro.cas.relay-state-parameter-name=state", "shiro.cas.role-attribute=groups",
                        "shiro.cas.ticket-validator-class=org.jasig.cas.client.validation.Cas20ProxyTicketValidator",
                        "shiro.cas.ssl-config-file=/fixture/tls.properties")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    FilterRegistrationBean<?> tickets = context.getBean("ticketValidationFilter", FilterRegistrationBean.class);
                    assertThat(tickets.getInitParameters())
                            .containsEntry(ConfigurationKeys.ACCEPT_ANY_PROXY.getName(), "false")
                            .containsEntry(ConfigurationKeys.ALLOWED_PROXY_CHAINS.getName(), "https://service.example.test/proxy")
                            .containsEntry(ConfigurationKeys.PROXY_RECEPTOR_URL.getName(), "/proxy")
                            .containsEntry(ConfigurationKeys.RELAY_STATE_PARAMETER_NAME.getName(), "state")
                            .containsEntry(ConfigurationKeys.ROLE_ATTRIBUTE.getName(), "groups")
                            .containsEntry(ConfigurationKeys.SSL_CONFIG_FILE.getName(), "/fixture/tls.properties");
                    FilterRegistrationBean<?> authentication = context.getBean("authenticationFilter", FilterRegistrationBean.class);
                    assertThat(authentication.getInitParameters())
                            .containsEntry(ConfigurationKeys.IGNORE_PATTERN.getName(), "/assets/.*")
                            .containsEntry(ConfigurationKeys.IGNORE_URL_PATTERN_TYPE.getName(), "REGEX");
                    FilterRegistrationBean<?> logout = context.getBean("casLogout", FilterRegistrationBean.class);
                    assertThat(((CasLogoutFilter) logout.getFilter()).getRedirectUrl()).isEqualTo("/local-login");
                });
    }

    static Stream<Arguments> protocolFilters() {
        return Stream.of(Arguments.of(Protocol.CAS1, Cas10TicketValidationFilter.class),
                Arguments.of(Protocol.CAS2, Cas20ProxyReceivingTicketValidationFilter.class),
                Arguments.of(Protocol.CAS3, Cas30ProxyReceivingTicketValidationFilter.class),
                Arguments.of(Protocol.SAML11, Saml11TicketValidationFilter.class));
    }

}
