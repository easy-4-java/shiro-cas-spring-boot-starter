package org.apache.shiro.spring.boot.utils;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Stream;

import org.apache.shiro.spring.boot.ShiroCasProperties;
import org.apache.shiro.spring.boot.cas.CasClientProperties;
import org.jasig.cas.client.Protocol;
import org.jasig.cas.client.validation.Cas10TicketValidator;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.Cas30ProxyTicketValidator;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.jasig.cas.client.validation.Saml11TicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class CasClientIntegrationTest {

    @ParameterizedTest
    @MethodSource("validators")
    void selectsTheValidatorForEachSupportedProtocol(Protocol protocol, boolean proxy,
                                                      Class<? extends TicketValidator> expected) {
        ShiroCasProperties properties = new ShiroCasProperties();
        properties.setCasServerUrlPrefix("https://sso.example.test/cas");
        properties.setProtocol(protocol);
        if (proxy) {
            properties.setAllowedProxyChains("https://service.example.test/proxy");
        }

        assertThat(CasTicketValidatorUtils.createTicketValidator(properties)).isExactlyInstanceOf(expected);
    }

    static Stream<Arguments> validators() {
        return Stream.of(Arguments.of(Protocol.CAS1, false, Cas10TicketValidator.class),
                Arguments.of(Protocol.CAS2, false, Cas20ServiceTicketValidator.class),
                Arguments.of(Protocol.CAS2, true, Cas20ProxyTicketValidator.class),
                Arguments.of(Protocol.CAS3, false, Cas30ServiceTicketValidator.class),
                Arguments.of(Protocol.CAS3, true, Cas30ProxyTicketValidator.class),
                Arguments.of(Protocol.SAML11, false, Saml11TicketValidator.class));
    }

    @Test
    void loadsSslPropertiesFromTheConfiguredFile(@TempDir Path folder) throws Exception {
        Path propertiesFile = folder.resolve("tls.properties");
        Files.write(propertiesFile, Collections.singletonList("protocol=TLSv1.2"), StandardCharsets.US_ASCII);
        ShiroCasProperties properties = new ShiroCasProperties();
        properties.setSslConfigFile(propertiesFile.toString());

        assertThat(CasTicketValidatorUtils.getSSLConfig(properties).getProperty("protocol")).isEqualTo("TLSv1.2");
    }

    @Test
    void explicitServiceUrlTakesPrecedenceOverServerName() {
        CasClientProperties properties = new CasClientProperties();
        properties.setServerName("https://gateway.example.test");
        properties.setService("https://client.example.test/cas/callback");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/reports");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThat(CasUrlUtils.constructServiceUrl(request, response, properties))
                .isEqualTo("https://client.example.test/cas/callback");
    }

    @Test
    void constructsLoginAndLogoutRedirectsWithTheApplicationCallback() throws Exception {
        CasClientProperties properties = new CasClientProperties();
        properties.setServerName("https://client.example.test:8443");
        properties.setCasServerUrlPrefix("https://sso.example.test/cas");
        properties.setCasServerLoginUrl("https://sso.example.test/cas/login");
        properties.setCasServerLogoutUrl("https://sso.example.test/cas/logout");

        URI login = URI.create(CasUrlUtils.constructLoginRedirectUrl(properties, "/app/", "/callback"));
        URI logout = URI.create(CasUrlUtils.constructLogoutRedirectUrl(properties, "/app", "/signed-out"));

        assertThat(login.getPath()).isEqualTo("/cas/login");
        assertThat(URLDecoder.decode(login.getRawQuery(), "UTF-8"))
                .contains("service=https://client.example.test:8443/app/callback");
        assertThat(logout.getPath()).isEqualTo("/cas/logout");
        assertThat(URLDecoder.decode(logout.getRawQuery(), "UTF-8"))
                .contains("service=https://client.example.test:8443/app/signed-out");
        assertThat(CasUrlUtils.constructCallbackUrl("/", "/callback")).isEqualTo("/callback");
        assertThat(CasUrlUtils.constructRedirectUrl(properties, "login", "/app", "/callback")).isEqualTo(login.toString());
    }
}
