package com.oceanprotocol.squid.exceptions;

/**
 * Business Exception related with Ethereum interactions issues
 */
public class EthereumOceanException extends OceanException{


    public EthereumOceanException(String message, Throwable e) {
        super(message, e);
    }
}
