package com.oceanprotocol.squid.exceptions;


/**
 * Business Exception related with Service Agreement issues
 */
public class ServiceAgreementOceanException extends OceanException {

    private String serviceAgreementId;

    public ServiceAgreementOceanException(String serviceAgreementId, String message, Throwable e) {

        super(message, e);
        this.serviceAgreementId = serviceAgreementId;

    }
}
