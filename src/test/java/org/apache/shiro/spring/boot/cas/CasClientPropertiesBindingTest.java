package org.apache.shiro.spring.boot.cas;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jasig.cas.client.Protocol;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

class CasClientPropertiesBindingTest {

    @Test
    void bindsServerLocationsAndRedirectPolicyForTheStandaloneClient() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("configuration-strategy", "WEB_XML");
        values.put("cas-server-login-url", "https://sso.example.test/cas/login");
        values.put("cas-server-logout-url", "https://sso.example.test/cas/logout");
        values.put("cas-server-rest-url", "https://sso.example.test/cas/v1/tickets");
        values.put("cas-server-url-prefix", "https://sso.example.test/cas");
        values.put("server-callback-url", "/callback");
        values.put("server-name", "https://service.example.test");
        values.put("service", "https://service.example.test/callback");
        values.put("service-parameter-name", "target");
        values.put("default-error-redirect-page", "/errors/default");
        values.put("error-redirect", "true");
        values.put("error-redirect-mappings.[java.io.IOException]", "/errors/unavailable");
        values.put("error-redirect-filter-url-patterns", "/callback,/validate");
        values.put("failure-url", "/login-failed");
        values.put("authentication-redirect-strategy-class", "com.example.RedirectStrategy");
        values.put("encode-service-url", "false");
        values.put("gateway", "true");
        values.put("gateway-storage-class", "com.example.GatewayStorage");
        values.put("ignore-case", "true");
        values.put("ignore-pattern", "/public/.*");
        values.put("ignore-url-pattern-type", "REGEX");
        values.put("ignore-init-configuration", "true");
        values.put("redirect-after-validation", "false");
        values.put("renew", "true");

        CasClientProperties client = bind(values);

        assertThat(client.getConfigurationStrategy()).isEqualTo("WEB_XML");
        assertThat(client.getCasServerLoginUrl()).isEqualTo("https://sso.example.test/cas/login");
        assertThat(client.getCasServerLogoutUrl()).isEqualTo("https://sso.example.test/cas/logout");
        assertThat(client.getCasServerRestUrl()).isEqualTo("https://sso.example.test/cas/v1/tickets");
        assertThat(client.getCasServerUrlPrefix()).isEqualTo("https://sso.example.test/cas");
        assertThat(client.getServerCallbackUrl()).isEqualTo("/callback");
        assertThat(client.getServerName()).isEqualTo("https://service.example.test");
        assertThat(client.getService()).isEqualTo("https://service.example.test/callback");
        assertThat(client.getServiceParameterName()).isEqualTo("target");
        assertThat(client.getDefaultErrorRedirectPage()).isEqualTo("/errors/default");
        assertThat(client.isErrorRedirect()).isTrue();
        assertThat(client.getErrorRedirectMappings()).containsExactlyEntriesOf(
                Collections.singletonMap("java.io.IOException", "/errors/unavailable"));
        assertThat(client.getErrorRedirectFilterUrlPatterns()).containsExactly("/callback", "/validate");
        assertThat(client.getFailureUrl()).isEqualTo("/login-failed");
        assertThat(client.getAuthenticationRedirectStrategyClass()).isEqualTo("com.example.RedirectStrategy");
        assertThat(client.isEncodeServiceUrl()).isFalse();
        assertThat(client.isGateway()).isTrue();
        assertThat(client.getGatewayStorageClass()).isEqualTo("com.example.GatewayStorage");
        assertThat(client.isIgnoreCase()).isTrue();
        assertThat(client.getIgnorePattern()).isEqualTo("/public/.*");
        assertThat(client.getIgnoreUrlPatternType()).isEqualTo("REGEX");
        assertThat(client.isIgnoreInitConfiguration()).isTrue();
        assertThat(client.isRedirectAfterValidation()).isFalse();
        assertThat(client.isRenew()).isTrue();
    }

    @Test
    void bindsProtocolProxyAndSessionValidationPolicy() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("protocol", "CAS3");
        values.put("enabled", "true");
        values.put("encoding", "ISO-8859-1");
        values.put("exception-on-validation-failure", "false");
        values.put("accept-any-proxy", "false");
        values.put("allowed-proxy-chains", "https://service.example.test/proxy");
        values.put("artifact-parameter-name", "serviceTicket");
        values.put("artifact-parameter-over-post", "true");
        values.put("cipher-algorithm", "AES");
        values.put("hostname-verifier", "com.example.Verifier");
        values.put("hostname-verifier-config", "service.example.test");
        values.put("proxy-callback-url", "https://service.example.test/proxy");
        values.put("proxy-receptor-url", "/proxy");
        values.put("proxy-granting-ticket-storage-class", "com.example.ProxyStorage");
        values.put("ssl-config-file", "/fixtures/cas-tls.properties");
        values.put("ticket-validator-class", "com.example.Validator");
        values.put("tolerance", "8000");
        values.put("eagerly-create-sessions", "false");
        values.put("use-session", "false");
        values.put("logout-parameter-name", "logout");
        values.put("millis-between-clean-ups", "90000");
        values.put("relay-state-parameter-name", "state");
        values.put("role-attribute", "groups");
        values.put("remember-me-attribute-name", "remembered");
        values.put("secret-key", "fixture-value");

        CasClientProperties client = bind(values);

        assertThat(client.getProtocol()).isEqualTo(Protocol.CAS3);
        assertThat(client.isEnabled()).isTrue();
        assertThat(client.getEncoding()).isEqualTo("ISO-8859-1");
        assertThat(client.isExceptionOnValidationFailure()).isFalse();
        assertThat(client.isAcceptAnyProxy()).isFalse();
        assertThat(client.getAllowedProxyChains()).isEqualTo("https://service.example.test/proxy");
        assertThat(client.getArtifactParameterName()).isEqualTo("serviceTicket");
        assertThat(client.isArtifactParameterOverPost()).isTrue();
        assertThat(client.getCipherAlgorithm()).isEqualTo("AES");
        assertThat(client.getHostnameVerifier()).isEqualTo("com.example.Verifier");
        assertThat(client.getHostnameVerifierConfig()).isEqualTo("service.example.test");
        assertThat(client.getProxyCallbackUrl()).isEqualTo("https://service.example.test/proxy");
        assertThat(client.getProxyReceptorUrl()).isEqualTo("/proxy");
        assertThat(client.getProxyGrantingTicketStorageClass()).isEqualTo("com.example.ProxyStorage");
        assertThat(client.getSslConfigFile()).isEqualTo("/fixtures/cas-tls.properties");
        assertThat(client.getTicketValidatorClass()).isEqualTo("com.example.Validator");
        assertThat(client.getTolerance()).isEqualTo(8_000L);
        assertThat(client.isEagerlyCreateSessions()).isFalse();
        assertThat(client.isUseSession()).isFalse();
        assertThat(client.getLogoutParameterName()).isEqualTo("logout");
        assertThat(client.getMillisBetweenCleanUps()).isEqualTo(90_000L);
        assertThat(client.getRelayStateParameterName()).isEqualTo("state");
        assertThat(client.getRoleAttribute()).isEqualTo("groups");
        assertThat(client.getRememberMeAttributeName()).isEqualTo("remembered");
        assertThat(client.getSecretKey()).isEqualTo("fixture-value");
    }

    @Test
    void bindsSeparateServletMappingsForEachFilter() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("assertion-thread-local-filter-url-patterns", "/callback,/assert");
        values.put("authentication-filter-url-patterns", "/login,/reports/*");
        values.put("request-wrapper-filter-url-patterns", "/reports/*");
        values.put("sign-out-filter-url-patterns", "/logout");
        values.put("ticket-validation-filter-url-patterns", "/callback");

        CasClientProperties client = bind(values);

        assertThat(client.getAssertionThreadLocalFilterUrlPatterns()).containsExactly("/callback", "/assert");
        assertThat(client.getAuthenticationFilterUrlPatterns()).containsExactly("/login", "/reports/*");
        assertThat(client.getRequestWrapperFilterUrlPatterns()).containsExactly("/reports/*");
        assertThat(client.getSignOutFilterUrlPatterns()).containsExactly("/logout");
        assertThat(client.getTicketValidationFilterUrlPatterns()).containsExactly("/callback");
    }

    private CasClientProperties bind(Map<String, String> values) {
        MapConfigurationPropertySource source = new MapConfigurationPropertySource();
        values.forEach((name, value) -> source.put("cas." + name, value));
        return new Binder(source).bind("cas", CasClientProperties.class).get();
    }
}
