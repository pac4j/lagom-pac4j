package org.pac4j.lagom.javadsl;

import com.lightbend.lagom.javadsl.api.deser.RawExceptionMessage;
import com.lightbend.lagom.javadsl.api.transport.TransportException;
import com.lightbend.lagom.javadsl.jackson.JacksonExceptionSerializer;
import org.pac4j.lagom.javadsl.transport.Unauthorized;
import play.Environment;

import static play.Mode.PROD;

/**
 * Serializer for unauthorized exception.
 *
 * @author Sergey Morgunov
 * @since 2.2.0
 */
public class Pac4jExceptionSerializer extends JacksonExceptionSerializer {

    public Pac4jExceptionSerializer() {
        super(new Environment(PROD));
    }

    public Pac4jExceptionSerializer(Environment environment) {
        super(environment);
    }

    @Override
    public Throwable deserialize(RawExceptionMessage message) {
        Throwable deserialize = super.deserialize(message);
        if (deserialize instanceof TransportException) {
            TransportException transportException = (TransportException) deserialize;
            if (Unauthorized.ERROR_CODE.equals(transportException.errorCode())) {
                deserialize = new Unauthorized(transportException.errorCode(), transportException.exceptionMessage());
            }
        }
        return deserialize;
    }
}
