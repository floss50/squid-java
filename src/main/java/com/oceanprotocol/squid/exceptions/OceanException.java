package com.oceanprotocol.squid.exceptions;

/**
 * Base Class to implement a hierarchy of Functional Ocean's Exceptions
 */
public class OceanException extends Exception {

    public OceanException(String message, Throwable e){
        super(message, e);
    }
}
