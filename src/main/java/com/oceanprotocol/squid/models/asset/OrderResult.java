package com.oceanprotocol.squid.models.asset;

public class OrderResult {

    private String serviceAgreementId;
    private Boolean accessGranted = false;
    private Boolean paymentRefund = false;

    public OrderResult(String serviceAgreementId, Boolean accessGranted, Boolean paymentRefund) {

        this.serviceAgreementId = serviceAgreementId;
        this.accessGranted = accessGranted;
        this.paymentRefund = paymentRefund;
    }

    public String getServiceAgreementId() {
        return serviceAgreementId;
    }

    public void setServiceAgreementId(String serviceAgreementId) {
        this.serviceAgreementId = serviceAgreementId;
    }

    public Boolean isAccessGranted() {
        return accessGranted;
    }

    public void setAccessGranted(Boolean accessGranted) {
        this.accessGranted = accessGranted;
    }

    public Boolean isPaymentRefund() {
        return paymentRefund;
    }

    public void setPaymentRefund(Boolean paymentRefund) {
        this.paymentRefund = paymentRefund;
    }
}
