package com.oceanprotocol.squid.api.config;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class OceanConfig {

    public static final String KEEPER_URL = "keeper.url";
    public static final String KEEPER_GAS_LIMIT = "keeper.gasLimit";
    public static final String KEEPER_GAS_PRICE = "keeper.gasPrice";
    public static final String AQUARIUS_URL = "aquarius.url";
    public static final String SECRETSTORE_URL = "secretstore.url";
    public static final String DID_REGISTRY_ADDRESS = "contract.didRegistry.address";
    public static final String SERVICE_AGREEMENT_ADDRESS = "contract.serviceAgreement.address";
    public static final String PAYMENT_CONDITIONS_ADDRESS = "contract.paymentConditions.address";
    public static final String ACCESS_CONDITIONS_ADDRESS = "contract.accessConditions.address";
    public static final String CONSUME_BASE_PATH = "consume.basePath";


    private String keeperUrl = "http://localhost:8545" ;
    private BigInteger keeperGasLimit = BigInteger.valueOf(4712388l) ;
    private BigInteger keeperGasPrice = BigInteger.valueOf(100000000000l);
    private String aquariusUrl = "http://localhost:5000";
    private String secretStoreUrl = "http://localhost:12001";
    private String didRegistryAddress;
    private String serviceAgreementAddress;
    private String paymentConditionsAddress;
    private String accessConditionsAddress;
    private String consumeBasePath = "/tmp";

    public static class OceanConfigValidation {

        private Boolean valid;
        private List<String> errors = new ArrayList<>();

        public Boolean isValid() {
            return valid;
        }

        public void setValid(Boolean valid) {
            this.valid = valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public void addErrorMessage(String error) {
            errors.add(error);
        }

    }


    public static OceanConfigValidation validate(OceanConfig oceanConfig) {

        OceanConfigValidation validation = new OceanConfigValidation();

        if (oceanConfig.getServiceAgreementAddress() == null || oceanConfig.getServiceAgreementAddress().isEmpty()) {
            validation.setValid(false);
            validation.addErrorMessage("The Address of Service Agreement Contract must be set with the property "
                    + OceanConfig.SERVICE_AGREEMENT_ADDRESS);
        }

        if (oceanConfig.getDidRegistryAddress() == null || oceanConfig.getDidRegistryAddress().isEmpty()) {
            validation.setValid(false);
            validation.addErrorMessage("The Address of DIDRegistry Contract must be set with the property "
                    + OceanConfig.DID_REGISTRY_ADDRESS);
        }

        if (oceanConfig.getPaymentConditionsAddress() == null || oceanConfig.getPaymentConditionsAddress().isEmpty()) {
            validation.setValid(false);
            validation.addErrorMessage("The Address of PaymentConditions Contract must be set with the property "
                    + OceanConfig.PAYMENT_CONDITIONS_ADDRESS);
        }

        if (oceanConfig.getAccessConditionsAddress() == null || oceanConfig.getAccessConditionsAddress().isEmpty()) {
            validation.setValid(false);
            validation.addErrorMessage("The Address of AccessConditions Contract must be set with the property "
                    + OceanConfig.ACCESS_CONDITIONS_ADDRESS);
        }

        return validation;
    }

    public String getKeeperUrl() {
        return keeperUrl;
    }

    public void setKeeperUrl(String keeperUrl) {
        this.keeperUrl = keeperUrl;
    }

    public BigInteger getKeeperGasLimit() {
        return keeperGasLimit;
    }

    public void setKeeperGasLimit(BigInteger keeperGasLimit) {
        this.keeperGasLimit = keeperGasLimit;
    }

    public BigInteger getKeeperGasPrice() {
        return keeperGasPrice;
    }

    public void setKeeperGasPrice(BigInteger keeperGasPrice) {
        this.keeperGasPrice = keeperGasPrice;
    }

    public String getAquariusUrl() {
        return aquariusUrl;
    }

    public void setAquariusUrl(String aquariusUrl) {
        this.aquariusUrl = aquariusUrl;
    }

    public String getSecretStoreUrl() {
        return secretStoreUrl;
    }

    public void setSecretStoreUrl(String secretStoreUrl) {
        this.secretStoreUrl = secretStoreUrl;
    }

    public String getDidRegistryAddress() {
        return didRegistryAddress;
    }

    public void setDidRegistryAddress(String didRegistryAddress) {
        this.didRegistryAddress = didRegistryAddress;
    }

    public String getServiceAgreementAddress() {
        return serviceAgreementAddress;
    }

    public void setServiceAgreementAddress(String serviceAgreementAddress) {
        this.serviceAgreementAddress = serviceAgreementAddress;
    }

    public String getPaymentConditionsAddress() {
        return paymentConditionsAddress;
    }

    public void setPaymentConditionsAddress(String paymentConditionsAddress) {
        this.paymentConditionsAddress = paymentConditionsAddress;
    }

    public String getAccessConditionsAddress() {
        return accessConditionsAddress;
    }

    public void setAccessConditionsAddress(String accessConditionsAddress) {
        this.accessConditionsAddress = accessConditionsAddress;
    }

    public String getConsumeBasePath() {
        return consumeBasePath;
    }

    public void setConsumeBasePath(String consumeBasePath) {
        this.consumeBasePath = consumeBasePath;
    }
}
