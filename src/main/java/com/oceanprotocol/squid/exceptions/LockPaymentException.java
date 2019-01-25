package com.oceanprotocol.squid.exceptions;

/**
 * Business Exception related with Lock Payment issues
 */
public class LockPaymentException extends OceanException {

    public LockPaymentException(String message, Throwable e) {
        super(message, e);
    }

    public LockPaymentException(String message) {
        super(message);
    }
}
