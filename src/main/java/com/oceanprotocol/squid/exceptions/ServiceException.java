package com.oceanprotocol.squid.exceptions;

/**
 * Business Exception related with Service issues
 */
public class ServiceException extends OceanException {

    public ServiceException(String message, Throwable e) {
        super(message, e);
    }

    public ServiceException(String message) {
        super(message);
    }
}
