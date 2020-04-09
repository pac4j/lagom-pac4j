package org.pac4j.lagom.javadsl.test;

import com.lightbend.lagom.javadsl.testkit.ServiceTest;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.lagom.javadsl.TestService;
import org.pac4j.lagom.jwt.JwtGeneratorHelper;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.startServer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.pac4j.lagom.javadsl.transport.RequestHeaderHelper.authorizationBearer;

/**
 * Test of security logic for {@link org.pac4j.http.client.direct.HeaderClient}
 * with {@link org.pac4j.jwt.credentials.authenticator.JwtAuthenticator}.
 *
 * @author Sergey Morgunov
 * @since 1.0.0
 */
class HeaderJwtClientTest {

    private static final Map<String, Object> ALICE_CLAIMS = new JWTClaimsSet.Builder()
        .issuer("https://pac4j.org")
        .subject("Alice")
        .issueTime(new Date())
        .jwtID(UUID.randomUUID().toString())
        .build()
        .getClaims();

    private static ServiceTest.TestServer server;

    private static TestService service;

    private static JwtGenerator<CommonProfile> octSignJwtGenerator;
    private static JwtGenerator<CommonProfile> rsaSignJwtGenerator;
    private static JwtGenerator<CommonProfile> ecSignJwtGenerator;

    private static JwtGenerator<CommonProfile> octEncryptJwtGenerator;
    private static JwtGenerator<CommonProfile> rsaEncryptJwtGenerator;
    private static JwtGenerator<CommonProfile> ecEncryptJwtGenerator;

    @BeforeAll
    static void beforeAll() throws ParseException, JOSEException {
        server = startServer(defaultSetup().withCluster(false));
        service = server.client(TestService.class);

        Config config = ConfigFactory.load();
        octSignJwtGenerator = JwtGeneratorHelper.parse(config.getConfig("pac4j.lagom.jwt.generator.sign.oct"));
        rsaSignJwtGenerator = JwtGeneratorHelper.parse(config.getConfig("pac4j.lagom.jwt.generator.sign.rsa"));
        ecSignJwtGenerator = JwtGeneratorHelper.parse(config.getConfig("pac4j.lagom.jwt.generator.sign.ec"));

        octEncryptJwtGenerator = JwtGeneratorHelper.parse(config.getConfig("pac4j.lagom.jwt.generator.encrypt.oct"));
        rsaEncryptJwtGenerator = JwtGeneratorHelper.parse(config.getConfig("pac4j.lagom.jwt.generator.encrypt.rsa"));
        ecEncryptJwtGenerator = JwtGeneratorHelper.parse(config.getConfig("pac4j.lagom.jwt.generator.encrypt.ec"));
    }

    @AfterAll
    static void afterAll() {
        if (server != null) server.stop();
    }

    @Test
    @DisplayName("authenticate by JWT with RSA signature")
    void testJwtRSASignature() throws ExecutionException, InterruptedException {
        String result = service.headerJwtAuthenticate()
            .handleRequestHeader(authorizationBearer(rsaSignJwtGenerator.generate(ALICE_CLAIMS)))
            .invoke().toCompletableFuture().get();
        assertThat(result).isEqualTo("Alice");
    }

    @Test
    @DisplayName("authenticate by JWT with RSA encryption")
    void testJwtRSAEncryption() throws ExecutionException, InterruptedException {
        String result = service.headerJwtAuthenticate()
            .handleRequestHeader(authorizationBearer(rsaEncryptJwtGenerator.generate(ALICE_CLAIMS)))
            .invoke().toCompletableFuture().get();
        assertThat(result).isEqualTo("Alice");
    }

    @Test
    @DisplayName("authenticate by JWT with Secret signature")
    void testJwtSecretSignature() throws ExecutionException, InterruptedException {
        String result = service.headerJwtAuthenticate()
            .handleRequestHeader(authorizationBearer(octSignJwtGenerator.generate(ALICE_CLAIMS)))
            .invoke().toCompletableFuture().get();
        assertThat(result).isEqualTo("Alice");
    }

    @Test
    @DisplayName("authenticate by JWT with Secret encryption")
    void testJwtSecretEncryption() throws ExecutionException, InterruptedException {
        String result = service.headerJwtAuthenticate()
            .handleRequestHeader(authorizationBearer(octEncryptJwtGenerator.generate(ALICE_CLAIMS)))
            .invoke().toCompletableFuture().get();
        assertThat(result).isEqualTo("Alice");
    }

    @Test
    @DisplayName("authenticate by JWT with EC signature")
    void testJwtECSignature() throws ExecutionException, InterruptedException {
        String result = service.headerJwtAuthenticate()
            .handleRequestHeader(authorizationBearer(ecSignJwtGenerator.generate(ALICE_CLAIMS)))
            .invoke().toCompletableFuture().get();
        assertThat(result).isEqualTo("Alice");
    }

    @Test
    @DisplayName("authenticate by JWT with EC encryption")
    void testJwtECEncryption() throws ExecutionException, InterruptedException {
        String result = service.headerJwtAuthenticate()
            .handleRequestHeader(authorizationBearer(ecEncryptJwtGenerator.generate(ALICE_CLAIMS)))
            .invoke().toCompletableFuture().get();
        assertThat(result).isEqualTo("Alice");
    }

}
