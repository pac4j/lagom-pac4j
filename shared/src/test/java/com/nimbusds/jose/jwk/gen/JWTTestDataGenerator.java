package com.nimbusds.jose.jwk.gen;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.ECDHEncrypter;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.util.Date;
import java.util.UUID;

import static com.nimbusds.jose.EncryptionMethod.A128GCM;
import static com.nimbusds.jose.EncryptionMethod.A256GCM;
import static com.nimbusds.jose.JWEAlgorithm.DIR;
import static com.nimbusds.jose.JWEAlgorithm.ECDH_ES_A256KW;
import static com.nimbusds.jose.JWEAlgorithm.RSA_OAEP_256;
import static com.nimbusds.jose.JWSAlgorithm.ES256;
import static com.nimbusds.jose.JWSAlgorithm.HS256;
import static com.nimbusds.jose.JWSAlgorithm.RS256;
import static com.nimbusds.jose.jwk.Curve.P_256;

/**
 * Generate data for test JWT.
 *
 * @author Sergey Morgunov
 * @since 1.0.0
 */
public class JWTTestDataGenerator {

    private static JWTClaimsSet jwtClaims = new JWTClaimsSet.Builder()
            .issuer("https://pac4j.org")
            .subject("Alice")
            .issueTime(new Date())
            .jwtID(UUID.randomUUID().toString())
            .build();

    private static void generateEC() throws JOSEException {
        System.out.println("::: EC :::");
        // Generate key
        ECKey jwk = new ECKeyGenerator(P_256).generate();
        ECKey publicJWK = jwk.toPublicJWK();
        System.out.println("  JWK: " + jwk.toJSONString());
        System.out.println("  PUB JWK: " + publicJWK.toJSONString());

        // Sign
        JWSHeader jwsHeader = new JWSHeader(ES256);
        SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaims);
        signedJWT.sign(new ECDSASigner(jwk));
        System.out.println("  JWT: " + signedJWT.serialize());

        // Encrypt
        JWEHeader jweHeader = new JWEHeader(ECDH_ES_A256KW, A256GCM);
        EncryptedJWT jwe = new EncryptedJWT(jweHeader, jwtClaims);
        jwe.encrypt(new ECDHEncrypter(jwk));
        System.out.println("  JWE: " + jwe.serialize());
    }

    private static void generateSecret() throws JOSEException {
        System.out.println("::: SECRET :::");
        // Generate key
        OctetSequenceKey jwk = new OctetSequenceKeyGenerator(256).algorithm(HS256).generate();
        System.out.println("  JWK: " + jwk.toJSONString());

        // Sign
        JWSHeader jwsHeader = new JWSHeader(HS256);
        SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaims);
        signedJWT.sign(new MACSigner(jwk));
        System.out.println("  JWT: " + signedJWT.serialize());

        // Encrypt
        JWEHeader jweHeader = new JWEHeader(DIR, A256GCM);
        EncryptedJWT jwe = new EncryptedJWT(jweHeader, jwtClaims);
        jwe.encrypt(new DirectEncrypter(jwk));
        System.out.println("  JWE: " + jwe.serialize());
    }

    private static void generateRSA() throws JOSEException {
        System.out.println("::: RSA :::");
        // Generate key
        RSAKey jwk = new RSAKeyGenerator(2048).generate();
        RSAKey publicJWK = jwk.toPublicJWK();
        System.out.println("  JWK: " + jwk.toJSONString());
        System.out.println("  PUB JWK: " + publicJWK.toJSONString());

        // Sign
        JWSHeader jwsHeader = new JWSHeader(RS256);
        SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaims);
        signedJWT.sign(new RSASSASigner(jwk));
        System.out.println("  JWT: " + signedJWT.serialize());

        // Encrypt
        JWEHeader jweHeader = new JWEHeader(RSA_OAEP_256, A128GCM);
        EncryptedJWT jwe = new EncryptedJWT(jweHeader, jwtClaims);
        jwe.encrypt(new RSAEncrypter(publicJWK));
        System.out.println("  JWE: " + jwe.serialize());
    }

    public static void main(String[] args) throws JOSEException {
        generateSecret();
        System.out.println();
        generateRSA();
        System.out.println();
        generateEC();
    }

}
