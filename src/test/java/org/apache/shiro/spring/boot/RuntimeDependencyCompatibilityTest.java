package org.apache.shiro.spring.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class RuntimeDependencyCompatibilityTest {

    @Test
    void bootLoadsNestedYamlProperties() throws Exception {
        String yaml = "shiro:\n  enabled: true\n  paths:\n    - /login\n"
                + "---\nshiro:\n  enabled: false\n";
        List<PropertySource<?>> sources = new YamlPropertySourceLoader()
                .load("security", new ByteArrayResource(yaml.getBytes(StandardCharsets.UTF_8)));

        assertThat(sources).hasSize(2);
        assertThat(sources.get(0).getProperty("shiro.enabled")).isEqualTo(true);
        assertThat(sources.get(0).getProperty("shiro.paths[0]")).isEqualTo("/login");
        assertThat(sources.get(1).getProperty("shiro.enabled")).isEqualTo(false);
    }

    @Test
    void springJacksonBuilderStillRoundTripsJavaTimeValues() throws Exception {
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
        Instant instant = Instant.parse("2026-11-18T12:00:00Z");

        assertThat(mapper.readValue(mapper.writeValueAsBytes(instant), Instant.class)).isEqualTo(instant);
    }
}
