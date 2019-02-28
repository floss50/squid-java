package com.oceanprotocol.squid.models.asset;

public class OrderResult {

    private String serviceAgreementId;
    private Boolean accessFullfilled = false;
    private Boolean timeout = false;

    public OrderResult(String serviceAgreementId, Boolean accessFullfilled, Boolean timeout) {

        this.serviceAgreementId = serviceAgreementId;
        this.accessFullfilled = accessFullfilled;
        this.timeout = timeout;
    }

    public String getServiceAgreementId() {
        return serviceAgreementId;
    }

    public void setServiceAgreementId(String serviceAgreementId) {
        this.serviceAgreementId = serviceAgreementId;
    }

    public Boolean isAccessGranted() {
        return accessFullfilled;
    }

    public void setAccessFullfilled(Boolean accessFullfilled) {
        this.accessFullfilled = accessFullfilled;
    }

    public Boolean isPaymentRefund() {
        return timeout;
    }

    public void setTimeout(Boolean timeout) {
        this.timeout = timeout;
    }
}
