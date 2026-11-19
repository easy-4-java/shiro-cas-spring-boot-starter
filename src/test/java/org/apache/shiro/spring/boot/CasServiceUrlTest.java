package org.apache.shiro.spring.boot;

import org.apache.shiro.spring.boot.cas.CasClientProperties;
import org.apache.shiro.spring.boot.utils.CasUrlUtils;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class CasServiceUrlTest {

    @Test
    void usesTheConfiguredServiceBeforeTheServerName() {
        var properties = new CasClientProperties();
        properties.setServerName("https://other.example.invalid");
        properties.setService("https://app.example.invalid/callback");

        assertThat(CasUrlUtils.constructServiceUrl(
                new MockHttpServletRequest("GET", "/ignored"), new MockHttpServletResponse(), properties))
                .isEqualTo("https://app.example.invalid/callback");
    }

    @Test
    void derivesTheServiceFromTheJakartaRequestAndKeepsBusinessParameters() {
        var properties = new CasClientProperties();
        properties.setArtifactParameterName("casTicket");
        properties.setServiceParameterName("callbackService");
        var request = new MockHttpServletRequest("GET", "/callback");
        request.setScheme("https");
        request.setSecure(true);
        request.setServerName("app.example.invalid");
        request.setServerPort(443);
        request.setQueryString("tenant=blue&casTicket=ST-fixture&callbackService=ignored&locale=zh");

        assertThat(CasUrlUtils.constructServiceUrl(request, new MockHttpServletResponse(), properties))
                .isEqualTo("https://app.example.invalid/callback?tenant=blue&locale=zh");
    }
}
