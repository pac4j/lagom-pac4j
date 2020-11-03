package org.pac4j.lagom.javadsl.transport;

import com.lightbend.lagom.javadsl.api.deser.ExceptionMessage;
import com.lightbend.lagom.javadsl.api.transport.TransportErrorCode;
import com.lightbend.lagom.javadsl.api.transport.TransportException;

/**
 * Exception thrown when a service call is unauthorized.
 *
 * @author Sergey Morgunov
 * @since 2.2.0
 */
public class Unauthorized extends TransportException {

    private static final long serialVersionUID = 1L;

    public static final TransportErrorCode ERROR_CODE = TransportErrorCode.fromHttp(401);

    public Unauthorized(String message) {
        super(ERROR_CODE, message);
    }

    public Unauthorized(Throwable cause) {
        super(ERROR_CODE, cause);
    }

    public Unauthorized(TransportErrorCode errorCode, ExceptionMessage exceptionMessage) {
        super(errorCode, exceptionMessage);
    }
}
