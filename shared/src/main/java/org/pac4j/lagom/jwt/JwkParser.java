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

import java.text.ParseException;

/**
 * @author Sergey Morgunov {@literal <smorgunov@at-consulting.ru>}
 */
class JwkParser {

    static AbstractEncryptionConfiguration parseEncryption(JWK jwk) throws JOSEException {
        AbstractEncryptionConfiguration encryption = null;
        if (jwk instanceof OctetSequenceKey) {
            encryption = new SecretEncryptionConfiguration(((OctetSequenceKey) jwk).toByteArray());
        } else if (jwk instanceof RSAKey) {
            encryption = new RSAEncryptionConfiguration(((RSAKey) jwk).toKeyPair());
        } else if (jwk instanceof ECKey) {
            encryption = new ECEncryptionConfiguration(((ECKey) jwk).toKeyPair());
        }
        return encryption;
    }

    static EncryptionConfiguration parseEncryption(Config conf) throws ParseException, JOSEException {
        if (!conf.hasPath("jwk")) return null;
        AbstractEncryptionConfiguration encryption = parseEncryption(
            JWK.parse(conf.getConfig("jwk").root().render(ConfigRenderOptions.concise()))
        );
        if (encryption != null) {
            if (conf.hasPath("algorithm")) encryption.setAlgorithm(JWEAlgorithm.parse(conf.getString("algorithm")));
            if (conf.hasPath("method")) encryption.setMethod(EncryptionMethod.parse(conf.getString("method")));
        }
        return encryption;
    }

    static AbstractSignatureConfiguration parseSignature(JWK jwk) throws JOSEException {
        AbstractSignatureConfiguration signature = null;
        if (jwk instanceof OctetSequenceKey) {
            signature = new SecretSignatureConfiguration(((OctetSequenceKey) jwk).toByteArray());
        } else if (jwk instanceof RSAKey) {
            signature = new RSASignatureConfiguration(((RSAKey) jwk).toKeyPair());
        } else if (jwk instanceof ECKey) {
            signature = new ECSignatureConfiguration(((ECKey) jwk).toKeyPair());
        }
        return signature;
    }

    static SignatureConfiguration parseSignature(Config conf) throws ParseException, JOSEException {
        if (!conf.hasPath("jwk")) return null;
        AbstractSignatureConfiguration signature = parseSignature(
            JWK.parse(conf.getConfig("jwk").root().render(ConfigRenderOptions.concise()))
        );
        if (signature != null && conf.hasPath("algorithm")) {
            signature.setAlgorithm(JWSAlgorithm.parse(conf.getString("algorithm")));
        }
        return signature;
    }

    private JwkParser() {
    }
}
