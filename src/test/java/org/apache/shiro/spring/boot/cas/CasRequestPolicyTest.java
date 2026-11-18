package org.apache.shiro.spring.boot.cas;

import org.apache.shiro.spring.boot.cas.token.CasAssertionAuthenticationToken;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.subject.support.DefaultWebSubjectContext;
import org.jasig.cas.client.authentication.UrlPatternMatcherStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class CasRequestPolicyTest {

    @Test
    void antMatcherAcceptsOnlyConfiguredPaths() {
        UrlPatternMatcherStrategy matcher = new AntPatternMatcherStrategy();
        matcher.setPattern("/assets/**; /favicon.ico");

        assertThat(matcher.matches("/assets/css/app.css")).isTrue();
        assertThat(matcher.matches("/favicon.ico")).isTrue();
        assertThat(matcher.matches("/reports")).isFalse();
    }

    @Test
    void containsMatcherChecksEachConfiguredFragment() {
        UrlPatternMatcherStrategy matcher = new ContainsPatternsUrlPatternMatcherStrategy();
        matcher.setPattern("/public/ , /assets/");

        assertThat(matcher.matches("/app/public/status")).isTrue();
        assertThat(matcher.matches("/app/assets/app.js")).isTrue();
        assertThat(matcher.matches("/app/reports")).isFalse();
    }

    @Test
    void neitherStrategyMatchesWhenPatternsAreAbsent() {
        UrlPatternMatcherStrategy ant = new AntPatternMatcherStrategy();
        UrlPatternMatcherStrategy contains = new ContainsPatternsUrlPatternMatcherStrategy();

        assertThat(ant.matches("/reports")).isFalse();
        assertThat(contains.matches("/reports")).isFalse();

        ant.setPattern(null);
        contains.setPattern(null);
        assertThat(ant.matches("/reports")).isFalse();
        assertThat(contains.matches("/reports")).isFalse();
    }

    @Test
    void remembersCasIdentityWithoutTreatingItAsAnInteractiveLogin() {
        DefaultWebSubjectContext context = new DefaultWebSubjectContext();
        context.setSecurityManager(new DefaultWebSecurityManager());
        context.setServletRequest(new MockHttpServletRequest());
        context.setServletResponse(new MockHttpServletResponse());
        context.setPrincipals(new SimplePrincipalCollection("auditor", "cas"));
        CasAssertionAuthenticationToken token = new CasAssertionAuthenticationToken("test-ticket", "127.0.0.1");
        context.setAuthenticationToken(token);
        context.setAuthenticated(true);
        CasSubjectFactory factory = new CasSubjectFactory();

        Subject authenticated = factory.createSubject(context);

        assertThat(authenticated.isAuthenticated()).isTrue();
        assertThat(authenticated.isRemembered()).isFalse();
        token.setRememberMe(true);

        Subject remembered = factory.createSubject(context);

        assertThat(remembered.getPrincipal()).isEqualTo("auditor");
        assertThat(remembered.isAuthenticated()).isFalse();
        assertThat(remembered.isRemembered()).isTrue();
    }
}
