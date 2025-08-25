package com.pagar.finance_api.core.security.jwt;

import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

@Component
public class JwkKeyManager {
    private final RSAKey rsaKey;

    public JwkKeyManager(@Value("${app.security.keys.public}") String publicKeyPath,
                         @Value("${app.security.keys.private}") String privateKeyPath) {
        try {
            RSAPublicKey publicKey = loadPublicKey(publicKeyPath);
            RSAPrivateKey privateKey = loadPrivateKey(privateKeyPath);
            this.rsaKey = new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(UUID.randomUUID().toString())
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao carregar as chaves RSA", e);
        }
    }

    public RSAKey getRsaKey() {
        return rsaKey;
    }

    private RSAPublicKey loadPublicKey(String classpathLocation) throws Exception {
        String pem = readClasspathFile(classpathLocation);
        String normalized = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] der = Base64.getDecoder().decode(normalized);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    private RSAPrivateKey loadPrivateKey(String classpathLocation) throws Exception {
        String pem = readClasspathFile(classpathLocation);
        String normalized = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] der = Base64.getDecoder().decode(normalized);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    private String readClasspathFile(String classpathLocation) throws Exception {
        ClassPathResource res = new ClassPathResource(classpathLocation);
        byte[] bytes = res.getInputStream().readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
