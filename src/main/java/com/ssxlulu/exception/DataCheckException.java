package com.ssxlulu.exception;

/**
 * DataCheckException.
 *
 * @author ssxlulu
 */
public class DataCheckException extends RuntimeException {

    public DataCheckException(final String message) {
        super(message);
    }

    public DataCheckException(final Throwable cause) {
        super(cause);
    }

    public DataCheckException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
