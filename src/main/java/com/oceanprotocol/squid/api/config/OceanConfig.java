package com.oceanprotocol.squid.api.config;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that keeps all the configurations to initialize the API
 */
public class OceanConfig {

    public static final String KEEPER_URL = "keeper.url";
    public static final String KEEPER_GAS_LIMIT = "keeper.gasLimit";
    public static final String KEEPER_GAS_PRICE = "keeper.gasPrice";
    public static final String AQUARIUS_URL = "aquarius.url";
    public static final String SECRETSTORE_URL = "secretstore.url";
    public static final String MAIN_ACCOUNT_ADDRESS = "account.main.address";
    public static final String MAIN_ACCOUNT_PASSWORD = "account.main.password";
    public static final String MAIN_ACCOUNT_CREDENTIALS_FILE = "account.main.credentialsFile";
    public static final String DID_REGISTRY_ADDRESS = "contract.didRegistry.address";
    public static final String SERVICE_EXECUTION_AGREEMENT_ADDRESS = "contract.serviceExecutionAgreement.address";
    public static final String PAYMENT_CONDITIONS_ADDRESS = "contract.paymentConditions.address";
    public static final String ACCESS_CONDITIONS_ADDRESS = "contract.accessConditions.address";
    public static final String TOKEN_ADDRESS = "contract.token.address";
    public static final String DISPENSER_ADDRESS = "contract.dispenser.address";
    public static final String CONSUME_BASE_PATH = "consume.basePath";


    private String keeperUrl;
    private BigInteger keeperGasLimit;
    private BigInteger keeperGasPrice;
    private String aquariusUrl;
    private String secretStoreUrl;
    private String mainAccountAddress;
    private String mainAccountPassword;
    private String mainAccountCredentialsFile;
    private String didRegistryAddress;
    private String serviceExecutionAgreementAddress;
    private String paymentConditionsAddress;
    private String accessConditionsAddress;
    private String tokenAddress;
    private String dispenserAddress;
    private String consumeBasePath;

    /**
     * Class to hold the result of a Configuration's validation
     */
    public static class OceanConfigValidation {

        private Boolean valid = true;
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

        public String errorsToString(){
            return String.join("; ", this.errors);
        }

    }


    /**
     * Validates that all the needed properties are set in the configuration
     * @param oceanConfig the configuration
     * @return an OceanConfigValidation object that indicates if the configuration is valid
     */
    public static OceanConfigValidation validate(OceanConfig oceanConfig) {

        OceanConfigValidation validation = new OceanConfigValidation();

        if (oceanConfig.getServiceExecutionAgreementAddress() == null || oceanConfig.getServiceExecutionAgreementAddress().isEmpty()) {
            validation.setValid(false);
            validation.addErrorMessage("The Address of Service Execution Agreement Contract must be set with the property "
                    + OceanConfig.SERVICE_EXECUTION_AGREEMENT_ADDRESS);
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

        if (oceanConfig.getMainAccountAddress() == null || oceanConfig.getMainAccountAddress().isEmpty()) {
            validation.setValid(false);
            validation.addErrorMessage("The Address of the Main Account must be set with the property "
                    + OceanConfig.MAIN_ACCOUNT_ADDRESS);
        }

        if (oceanConfig.getMainAccountPassword() == null) {
            validation.setValid(false);
            validation.addErrorMessage("The Password of the Main Account must be set with the property "
                    + OceanConfig.MAIN_ACCOUNT_PASSWORD);
        }

        if (oceanConfig.getMainAccountCredentialsFile() == null) {
            validation.setValid(false);
            validation.addErrorMessage("The Credentials File of the Main Account must be set with the property "
                    + OceanConfig.MAIN_ACCOUNT_CREDENTIALS_FILE);
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

    public String getServiceExecutionAgreementAddress() {
        return serviceExecutionAgreementAddress;
    }

    public void setServiceExecutionAgreementAddress(String serviceExecutionAgreementAddress) {
        this.serviceExecutionAgreementAddress = serviceExecutionAgreementAddress;
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

    public String getMainAccountAddress() {
        return mainAccountAddress;
    }

    public void setMainAccountAddress(String mainAccountAddress) {
        this.mainAccountAddress = mainAccountAddress;
    }

    public String getMainAccountPassword() {
        return mainAccountPassword;
    }

    public void setMainAccountPassword(String mainAccountPassword) {
        this.mainAccountPassword = mainAccountPassword;
    }

    public String getMainAccountCredentialsFile() {
        return mainAccountCredentialsFile;
    }

    public void setMainAccountCredentialsFile(String mainAccountCredentialsFile) {
        this.mainAccountCredentialsFile = mainAccountCredentialsFile;
    }

    public String getTokenAddress() {
        return tokenAddress;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
    }

    public String getDispenserAddress() {
        return dispenserAddress;
    }

    public void setDispenserAddress(String dispenserAddress) {
        this.dispenserAddress = dispenserAddress;
    }
}
