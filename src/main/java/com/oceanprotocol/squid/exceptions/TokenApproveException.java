package com.oceanprotocol.squid.exceptions;

public class TokenApproveException extends OceanException{

    public TokenApproveException(String message, Throwable e) {
        super(message, e);
    }

    public TokenApproveException(String message) {
        super(message);
    }
}
