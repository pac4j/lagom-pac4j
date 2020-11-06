package org.pac4j.lagom.jwt;

import com.nimbusds.jose.JOSEException;
import com.typesafe.config.Config;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jwt.profile.JwtGenerator;

import java.text.ParseException;

import static org.pac4j.lagom.jwt.JwkParser.parseEncryption;
import static org.pac4j.lagom.jwt.JwkParser.parseSignature;

/**
 * Helper for parsing {@link JwtGenerator} from Lagom configuration.
 *
 * @author Sergey Morgunov
 * @since 2.2.1
 */
public final class JwtGeneratorHelper {

    /**
     * Parse {@link JwtGenerator} from Lagom conf.
     *
     * @param conf Configuration of authenticator
     * @return JWT authenticator
     * @throws ParseException        a parse exception
     * @throws JOSEException         a signing/encryption exception
     */
    public static <T extends CommonProfile> JwtGenerator<T> parse(Config conf) throws ParseException, JOSEException {
        return new JwtGenerator<>(
            conf.hasPath("signature") ? parseSignature(conf.getConfig("signature")) : null,
            conf.hasPath("encryption") ? parseEncryption(conf.getConfig("encryption")) : null
        );
    }

    private JwtGeneratorHelper() {
    }
}
