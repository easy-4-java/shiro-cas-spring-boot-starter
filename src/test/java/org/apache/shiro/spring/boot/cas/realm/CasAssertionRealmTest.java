package org.apache.shiro.spring.boot.cas.realm;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.biz.authz.principal.ShiroPrincipalRepository;
import org.apache.shiro.spring.boot.ShiroCasProperties;
import org.apache.shiro.spring.boot.cas.exception.CasTicketFoundException;
import org.apache.shiro.spring.boot.cas.token.CasAssertionAuthenticationToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.jasig.cas.client.authentication.AttributePrincipalImpl;
import org.jasig.cas.client.util.AssertionHolder;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.AssertionImpl;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CasAssertionRealmTest {

    private static final String SERVICE = "https://service.example.test/cas/callback";
    private static final String SERVER = "https://service.example.test";
    private static final String TICKET = "test-ticket";

    private final ShiroCasProperties properties = new ShiroCasProperties();
    private final TicketValidator validator = mock(TicketValidator.class);
    private final CasAssertionSimpleAuthorizingRealm simpleRealm = new CasAssertionSimpleAuthorizingRealm(properties) {
        @Override
        protected TicketValidator ensureTicketValidator() {
            return validator;
        }
    };
    private final CasAssertionAuthorizingRealm repositoryRealm = new CasAssertionAuthorizingRealm(properties) {
        @Override
        protected TicketValidator ensureTicketValidator() {
            return validator;
        }
    };

    @BeforeEach
    void configureProperties() {
        properties.setServerName(SERVER);
        properties.setService(SERVICE);
    }

    @AfterEach
    void clearAssertion() {
        AssertionHolder.clear();
    }

    @Test
    void bothRealmsValidateAgainstTheExplicitService() throws Exception {
        Assertion assertion = assertionWithPermissions();
        when(validator.validate(TICKET, SERVICE)).thenReturn(assertion);
        CasAssertionAuthenticationToken simpleToken = token(TICKET);
        AuthenticationInfo simpleInfo = simpleRealm.doGetAuthenticationInfo(simpleToken);
        assertThat(simpleInfo.getPrincipals().getPrimaryPrincipal()).isEqualTo("auditor");
        assertThat(simpleToken.isRememberMe()).isTrue();

        ShiroPrincipalRepository repository = mock(ShiroPrincipalRepository.class);
        repositoryRealm.setRepository(repository);
        CasAssertionAuthenticationToken repositoryToken = token(TICKET);
        AuthenticationInfo expected = new SimpleAuthenticationInfo("auditor", TICKET, "repository");
        when(repository.getAuthenticationInfo(repositoryToken)).thenReturn(expected);

        assertThat(repositoryRealm.doGetAuthenticationInfo(repositoryToken)).isSameAs(expected);
        assertThat(repositoryToken.getAssertion()).isSameAs(assertion);
        assertThat(repositoryToken.isRememberMe()).isTrue();
        verify(repository).getAuthenticationInfo(repositoryToken);
    }

    @Test
    void simpleRealmReadsBooleanRememberMeAndMapsVerifiedAssertionPermissions() {
        AssertionHolder.setAssertion(assertionWithPermissions());
        simpleRealm.setDefaultRoles("employee, ");
        simpleRealm.setDefaultPermissions("account:read");
        simpleRealm.setRoleAttributeNames("groups, missing");
        simpleRealm.setPermissionAttributeNames("permissions, missing");
        CasAssertionAuthenticationToken token = token(null);

        PrincipalCollection principals = simpleRealm.doGetAuthenticationInfo(token).getPrincipals();

        assertThat(principals.getPrimaryPrincipal()).isEqualTo("auditor");
        assertThat(token.isRememberMe()).isTrue();
        assertThat(simpleRealm.hasRole(principals, "employee")).isTrue();
        assertThat(simpleRealm.hasRole(principals, "finance")).isTrue();
        assertThat(simpleRealm.isPermitted(principals, "account:read")).isTrue();
        assertThat(simpleRealm.isPermitted(principals, "reports:read")).isTrue();
        assertThat(simpleRealm.isPermitted(principals, "reports:write")).isFalse();
        verifyNoInteractions(validator);
    }

    @Test
    void missingTicketFailsBeforeContactingTheValidator() {
        assertThatThrownBy(() -> simpleRealm.doGetAuthenticationInfo(token(null))).isInstanceOf(CasTicketFoundException.class);
        assertThatThrownBy(() -> repositoryRealm.doGetAuthenticationInfo(token(null))).isInstanceOf(CasTicketFoundException.class);
        verifyNoInteractions(validator);
    }

    @Test
    void retainsTheValidationFailureWhenOnlyServerNameIsConfigured() throws Exception {
        properties.setService(null);
        TicketValidationException rejection = new TicketValidationException("fixture rejection");
        when(validator.validate(TICKET, SERVER)).thenThrow(rejection);

        assertThatThrownBy(() -> simpleRealm.doGetAuthenticationInfo(token(TICKET)))
                .isInstanceOf(AuthenticationException.class).hasCause(rejection);
        assertThatThrownBy(() -> repositoryRealm.doGetAuthenticationInfo(token(TICKET)))
                .isInstanceOf(AuthenticationException.class).hasCause(rejection);
    }

    private CasAssertionAuthenticationToken token(String ticket) {
        return new CasAssertionAuthenticationToken(ticket, "127.0.0.1");
    }

    private Assertion assertionWithPermissions() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(properties.getRememberMeAttributeName(), Boolean.TRUE);
        attributes.put("groups", "finance, auditors");
        attributes.put("permissions", "reports:read");
        return new AssertionImpl(new AttributePrincipalImpl("auditor", attributes));
    }
}
