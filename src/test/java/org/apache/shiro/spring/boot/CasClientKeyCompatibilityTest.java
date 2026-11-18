package org.apache.shiro.spring.boot;

import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.jasig.cas.client.util.PrivateKeyUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class CasClientKeyCompatibilityTest {

    @Test
    void casClientStillReadsTraditionalRsaPem(@TempDir Path directory) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        Path pem = directory.resolve("key.pem");
        try (Writer output = Files.newBufferedWriter(pem, StandardCharsets.US_ASCII);
             JcaPEMWriter pemWriter = new JcaPEMWriter(output)) {
            pemWriter.writeObject(keyPair);
        }

        PrivateKey restored = PrivateKeyUtils.createKey(pem.toString(), "RSA");

        assertThat(restored).isNotNull();
        assertThat(restored.getEncoded()).isEqualTo(keyPair.getPrivate().getEncoded());
    }
}
