package org.apache.shiro.spring.boot;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import com.sun.net.httpserver.HttpServer;
import org.apache.shiro.spring.boot.cas.exception.CasAuthenticationException;
import org.apache.shiro.spring.boot.cas.exception.CasTicketFoundException;
import org.apache.shiro.spring.boot.cas.realm.CasAssertionSimpleAuthorizingRealm;
import org.apache.shiro.spring.boot.cas.token.CasAssertionAuthenticationToken;
import org.apereo.cas.client.Protocol;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CasTicketExchangeTest {

    @ParameterizedTest
    @CsvSource({"CAS2,/cas/serviceValidate", "CAS3,/cas/p3/serviceValidate"})
    void exchangesAServiceTicketUsingTheConfiguredProtocol(Protocol protocol, String validationRoute) throws Exception {
        var query = new AtomicReference<String>();
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext(validationRoute, exchange -> {
            try (exchange) {
                query.set(exchange.getRequestURI().getQuery());
                String result = query.get().contains("ticket=ST-rejected") ? """
                        <cas:authenticationFailure code="INVALID_TICKET">Rejected</cas:authenticationFailure>
                        """ : """
                        <cas:authenticationSuccess>
                          <cas:user>test-user</cas:user>
                          <cas:attributes>
                            <cas:groups>finance, reports</cas:groups>
                            <cas:permissions>payroll:read</cas:permissions>
                          </cas:attributes>
                        </cas:authenticationSuccess>
                        """;
                byte[] payload = """
                        <cas:serviceResponse xmlns:cas="http://www.yale.edu/tp/cas">
                        %s
                        </cas:serviceResponse>
                        """.formatted(result).getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/xml; charset=UTF-8");
                exchange.sendResponseHeaders(200, payload.length);
                exchange.getResponseBody().write(payload);
            }
        });
        server.start();
        try {
            var properties = new ShiroCasProperties();
            properties.setProtocol(protocol);
            properties.setCasServerUrlPrefix("http://127.0.0.1:" + server.getAddress().getPort() + "/cas");
            properties.setEncoding("UTF-8");
            properties.setServerName("https://app.example.invalid/callback");
            var realm = new CasAssertionSimpleAuthorizingRealm(properties);
            realm.setDefaultRoles("employee");
            realm.setDefaultPermissions("account:read");
            realm.setRoleAttributeNames("groups");
            realm.setPermissionAttributeNames("permissions");
            var login = new CasAssertionAuthenticationToken("ST-test-ticket", "127.0.0.1");
            var authentication = realm.getAuthenticationInfo(login);
            assertThat(authentication.getPrincipals().getPrimaryPrincipal()).isEqualTo("test-user");
            assertThat(query.get()).contains("ticket=ST-test-ticket", "service=https://app.example.invalid/callback");
            assertThat(realm.hasRole(authentication.getPrincipals(), "employee")).isTrue();
            assertThat(realm.isPermitted(authentication.getPrincipals(), "account:read")).isTrue();
            assertThat(realm.isPermitted(authentication.getPrincipals(), "account:write")).isFalse();
            if (protocol == Protocol.CAS3) {
                assertThat(realm.hasRole(authentication.getPrincipals(), "reports")).isTrue();
                assertThat(realm.isPermitted(authentication.getPrincipals(), "payroll:read")).isTrue();
            }

            assertThatThrownBy(() -> realm.getAuthenticationInfo(new CasAssertionAuthenticationToken(
                    "ST-rejected", "127.0.0.1"))).isInstanceOf(CasAuthenticationException.class);
            assertThatThrownBy(() -> realm.getAuthenticationInfo(new CasAssertionAuthenticationToken(
                    " ", "127.0.0.1"))).isInstanceOf(CasTicketFoundException.class);
        } finally {
            server.stop(0);
        }
    }
}
