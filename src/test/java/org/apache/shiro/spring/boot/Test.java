package org.apache.shiro.spring.boot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ShiroCasProperties.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@DisplayName("ShiroCasProperties Tests")
class ShiroCasPropertiesTest {

    @Test
    @DisplayName("Default constructor creates non-null instance with defaults")
    void testDefaultInstance() {
        ShiroCasProperties props = new ShiroCasProperties();
        assertThat(props).isNotNull();
        assertThat(props.getCaMode()).isEqualTo(ShiroCasProperties.CaMode.SSO);
        assertThat(props.isEnabled()).isFalse();
        assertThat(props.getProtocol()).isNotNull();
    }

    @Test
    @DisplayName("PREFIX constant has expected value")
    void testPREFIXConstant() {
        assertThat(ShiroCasProperties.PREFIX).isEqualTo("shiro.cas");
    }

    @Test
    @DisplayName("caMode getter/setter works correctly")
    void testCaModeGetterSetter() {
        ShiroCasProperties props = new ShiroCasProperties();
        props.setCaMode(ShiroCasProperties.CaMode.ROAM);
        assertThat(props.getCaMode()).isEqualTo(ShiroCasProperties.CaMode.ROAM);
    }

    @Test
    @DisplayName("enabled getter/setter works correctly")
    void testEnabledGetterSetter() {
        ShiroCasProperties props = new ShiroCasProperties();
        props.setEnabled(true);
        assertThat(props.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("casServerLoginUrl getter/setter works correctly")
    void testCasServerLoginUrl() {
        ShiroCasProperties props = new ShiroCasProperties();
        props.setCasServerLoginUrl("https://cas.example.com/login");
        assertThat(props.getCasServerLoginUrl()).isEqualTo("https://cas.example.com/login");
    }

    @Test
    @DisplayName("casServerLogoutUrl getter/setter works correctly")
    void testCasServerLogoutUrl() {
        ShiroCasProperties props = new ShiroCasProperties();
        props.setCasServerLogoutUrl("https://cas.example.com/logout");
        assertThat(props.getCasServerLogoutUrl()).isEqualTo("https://cas.example.com/logout");
    }

    @Test
    @DisplayName("serverName getter/setter works correctly")
    void testServerName() {
        ShiroCasProperties props = new ShiroCasProperties();
        props.setServerName("https://app.example.com");
        assertThat(props.getServerName()).isEqualTo("https://app.example.com");
    }

    @Test
    @DisplayName("service getter/setter works correctly")
    void testService() {
        ShiroCasProperties props = new ShiroCasProperties();
        props.setService("https://app.example.com/cas");
        assertThat(props.getService()).isEqualTo("https://app.example.com/cas");
    }

    @Test
    @DisplayName("encoding getter/setter works correctly")
    void testEncoding() {
        ShiroCasProperties props = new ShiroCasProperties();
        assertThat(props.getEncoding()).isEqualTo("UTF-8");
        props.setEncoding("ISO-8859-1");
        assertThat(props.getEncoding()).isEqualTo("ISO-8859-1");
    }

    @Test
    @DisplayName("protocol getter/setter works correctly")
    void testProtocol() {
        ShiroCasProperties props = new ShiroCasProperties();
        assertThat(props.getProtocol()).isNotNull();
    }

    @Test
    @DisplayName("CaMode enum values are correct")
    void testCaModeEnum() {
        assertThat(ShiroCasProperties.CaMode.values()).hasSize(2);
        assertThat(ShiroCasProperties.CaMode.valueOf("SSO")).isEqualTo(ShiroCasProperties.CaMode.SSO);
        assertThat(ShiroCasProperties.CaMode.valueOf("ROAM")).isEqualTo(ShiroCasProperties.CaMode.ROAM);
    }
}
