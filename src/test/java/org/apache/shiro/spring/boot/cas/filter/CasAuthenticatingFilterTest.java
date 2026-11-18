package org.apache.shiro.spring.boot.cas.filter;

import java.util.Collections;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.biz.authc.AuthenticationFailureHandler;
import org.apache.shiro.biz.authc.AuthenticationSuccessHandler;
import org.apache.shiro.biz.authc.token.LoginType;
import org.apache.shiro.biz.web.filter.authc.listener.LoginListener;
import org.apache.shiro.spring.boot.cas.token.CasAssertionAuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CasAuthenticatingFilterTest {

    private final CasAuthenticatingFilter filter = new CasAuthenticatingFilter();
    private final Subject subject = mock(Subject.class);
    private final LoginListener listener = mock(LoginListener.class);
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/callback");
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private final MockFilterChain chain = new MockFilterChain();

    @BeforeEach
    void configureCallback() {
        filter.processPathConfig("/callback", null);
        filter.setSuccessUrl("/home");
        filter.setFailureUrl("/login-error");
        filter.setLoginListeners(Collections.singletonList(listener));
        request.setServletPath("/callback");
        request.setRemoteAddr("localhost");
        request.setRemoteUser("auditor");
    }

    @Test
    void successfulTicketCallsTheListenerAndRedirectsToTheConfiguredPage() throws Exception {
        request.setParameter("ticket", "test-ticket");

        dispatch();

        CasAssertionAuthenticationToken token = captureSubmittedToken();
        assertThat(token.getTicket()).isEqualTo("test-ticket");
        assertThat(token.getCredentials()).isEqualTo("test-ticket");
        assertThat(token.getPrincipal()).isEqualTo("auditor");
        assertThat(token.getHost()).isEqualTo("127.0.0.1");
        assertThat(token.getLoginType()).isEqualTo(LoginType.CAS);
        verify(listener).onSuccess(token, subject, request, response);
        assertThat(response.getRedirectedUrl()).isEqualTo("/home");
        assertThat(chain.getRequest()).isNull();
    }

    @Test
    void successfulTicketUsesTheMatchingApplicationHandler() throws Exception {
        AuthenticationSuccessHandler handler = mock(AuthenticationSuccessHandler.class);
        when(handler.supports(any(AuthenticationToken.class))).thenReturn(true);
        filter.setSuccessHandlers(Collections.singletonList(handler));
        request.setParameter("ticket", "test-ticket");

        dispatch();

        CasAssertionAuthenticationToken token = captureSubmittedToken();
        verify(handler).onAuthenticationSuccess(token, request, response, subject);
        assertThat(response.getRedirectedUrl()).isNull();
    }

    @Test
    void failedTicketUsesTheMatchingApplicationHandler() throws Exception {
        AuthenticationFailureHandler handler = mock(AuthenticationFailureHandler.class);
        AuthenticationException failure = new AuthenticationException("fixture rejection");
        when(handler.supports(failure)).thenReturn(true);
        doThrow(failure).when(subject).login(any(AuthenticationToken.class));
        filter.setFailureHandlers(Collections.singletonList(handler));

        dispatch();

        verify(handler).onAuthenticationFailure(any(AuthenticationToken.class), same(request), same(response), same(failure));
        verify(listener).onFailure(any(AuthenticationToken.class), same(failure), same(request), same(response));
        assertThat(response.getRedirectedUrl()).isNull();
        assertThat(chain.getRequest()).isNull();
    }

    @Test
    void failedTicketFallsBackToTheConfiguredErrorPageWithoutAHandler() throws Exception {
        doThrow(new AuthenticationException("fixture rejection")).when(subject).login(any(AuthenticationToken.class));

        dispatch();

        assertThat(response.getRedirectedUrl()).isEqualTo("/login-error");
        assertThat(chain.getRequest()).isNull();
    }

    private CasAssertionAuthenticationToken captureSubmittedToken() {
        ArgumentCaptor<AuthenticationToken> captor = ArgumentCaptor.forClass(AuthenticationToken.class);
        verify(subject).login(captor.capture());
        return (CasAssertionAuthenticationToken) captor.getValue();
    }

    private void dispatch() throws Exception {
        SubjectThreadState state = new SubjectThreadState(subject);
        state.bind();
        try {
            filter.doFilter(request, response, chain);
        } finally {
            state.restore();
        }
    }
}
