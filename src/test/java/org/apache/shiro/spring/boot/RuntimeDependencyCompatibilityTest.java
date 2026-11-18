package org.apache.shiro.spring.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class RuntimeDependencyCompatibilityTest {

    @Test
    void bootLoadsNestedYamlProperties() throws Exception {
        byte[] yaml = "shiro:\n  enabled: true\n".getBytes(StandardCharsets.UTF_8);
        PropertySource<?> source = new YamlPropertySourceLoader()
                .load("security", new ByteArrayResource(yaml)).get(0);

        assertThat(source.getProperty("shiro.enabled")).isEqualTo(true);
    }

    @Test
    void springJacksonBuilderStillRoundTripsJavaTimeValues() throws Exception {
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
        Instant instant = Instant.parse("2026-11-18T12:00:00Z");

        assertThat(mapper.readValue(mapper.writeValueAsBytes(instant), Instant.class)).isEqualTo(instant);
    }
}
