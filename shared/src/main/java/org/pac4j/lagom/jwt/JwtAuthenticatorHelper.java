package org.pac4j.lagom.jwt;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigRenderOptions;
import org.pac4j.jwt.config.encryption.AbstractEncryptionConfiguration;
import org.pac4j.jwt.config.encryption.ECEncryptionConfiguration;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.encryption.RSAEncryptionConfiguration;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.AbstractSignatureConfiguration;
import org.pac4j.jwt.config.signature.ECSignatureConfiguration;
import org.pac4j.jwt.config.signature.RSASignatureConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper for parsing {@link JwtAuthenticator} from Lagom configuration.
 *
 * @author Sergey Morgunov
 * @since 1.0.0
 */
public final class JwtAuthenticatorHelper {

    private static SignatureConfiguration parseSignature(Config conf) throws ParseException, JOSEException {
        if (!conf.hasPath("jwk")) return null;
        AbstractSignatureConfiguration signature = null;
        JWK jwk = JWK.parse(conf.getConfig("jwk").root().render(ConfigRenderOptions.concise()));
        if (jwk instanceof OctetSequenceKey) {
            signature = new SecretSignatureConfiguration(((OctetSequenceKey) jwk).toByteArray());
        } else if (jwk instanceof RSAKey) {
            signature = new RSASignatureConfiguration(((RSAKey) jwk).toKeyPair());
        } else if (jwk instanceof ECKey) {
            signature = new ECSignatureConfiguration(((ECKey) jwk).toKeyPair());
        }
        if (signature != null && conf.hasPath("algorithm")) {
            signature.setAlgorithm(JWSAlgorithm.parse(conf.getString("algorithm")));
        }
        return signature;
    }

    private static EncryptionConfiguration parseEncryption(Config conf) throws ParseException, JOSEException {
        if (!conf.hasPath("jwk")) return null;
        AbstractEncryptionConfiguration encryption = null;
        JWK jwk = JWK.parse(conf.getConfig("jwk").root().render(ConfigRenderOptions.concise()));
        if (jwk instanceof OctetSequenceKey) {
            encryption = new SecretEncryptionConfiguration(((OctetSequenceKey) jwk).toByteArray());
        } else if (jwk instanceof RSAKey) {
            encryption = new RSAEncryptionConfiguration(((RSAKey) jwk).toKeyPair());
        } else if (jwk instanceof ECKey) {
            encryption = new ECEncryptionConfiguration(((ECKey) jwk).toKeyPair());
        }
        if (encryption != null) {
            if (conf.hasPath("algorithm")) encryption.setAlgorithm(JWEAlgorithm.parse(conf.getString("algorithm")));
            if (conf.hasPath("method")) encryption.setMethod(EncryptionMethod.parse(conf.getString("method")));
        }
        return encryption;
    }

    /**
     * Parse {@link JwtAuthenticator} from Lagom conf.
     *
     * @param conf Configuration of authenticator
     * @return JWT authenticator
     * @throws ParseException
     * @throws JOSEException
     */
    public static JwtAuthenticator parse(Config conf) throws ParseException, JOSEException {
        List<SignatureConfiguration> signatures = new ArrayList<>();
        List<EncryptionConfiguration> encryptions = new ArrayList<>();
        if (conf.hasPath("signatures")) {
            for (Config signature : conf.getConfigList("signatures")) {
                SignatureConfiguration signatureConfiguration = parseSignature(signature);
                if (signatureConfiguration != null) signatures.add(signatureConfiguration);
            }
        }
        if (conf.hasPath("encryptions")) {
            for (Config signature : conf.getConfigList("encryptions")) {
                EncryptionConfiguration encryptionConfiguration = parseEncryption(signature);
                if (encryptionConfiguration != null) encryptions.add(encryptionConfiguration);
            }
        }
        return new JwtAuthenticator(signatures, encryptions);
    }

    private JwtAuthenticatorHelper() {}
}
