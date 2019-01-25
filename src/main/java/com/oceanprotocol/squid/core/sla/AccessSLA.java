package com.oceanprotocol.squid.core.sla;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oceanprotocol.keeper.contracts.AccessConditions;
import com.oceanprotocol.keeper.contracts.ServiceAgreement;
import com.oceanprotocol.squid.exceptions.InitializeConditionsException;
import com.oceanprotocol.squid.helpers.CryptoHelper;
import com.oceanprotocol.squid.helpers.EthereumHelper;
import com.oceanprotocol.squid.manager.BaseManager;
import com.oceanprotocol.squid.models.AbstractModel;
import com.oceanprotocol.squid.models.service.Condition;
import io.reactivex.Flowable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.datatypes.Event;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;

import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AccessSLA implements SlaFunctions {

    static final Logger log= LogManager.getLogger(AccessSLA.class);

    private static final String ACCESS_CONDITIONS_FILE_TEMPLATE= "src/main/resources/sla/sla-access-conditions-template.json";
    private String conditionsTemplate= null;

    public static Flowable<ServiceAgreement.ExecuteAgreementEventResponse> listenExecuteAgreement(ServiceAgreement slaContract, String serviceAgreementId)   {
        EthFilter slaFilter = new EthFilter(
                DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST,
                slaContract.getContractAddress()
        );

        final Event event= slaContract.EXECUTEAGREEMENT_EVENT;
        final String eventSignature= EventEncoder.encode(event);
        String slaTopic= "0x" + serviceAgreementId;
        slaFilter.addSingleTopic(eventSignature);
        slaFilter.addOptionalTopics(slaTopic);

        return slaContract.executeAgreementEventFlowable(slaFilter);
    }


    public static Flowable<AccessConditions.AccessGrantedEventResponse> listenForGrantedAccess(AccessConditions accessConditions,
                                                                                                  String serviceAgreementId)   {

        EthFilter grantedFilter = new EthFilter(
                DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST,
                accessConditions.getContractAddress()
        );

        final Event event= AccessConditions.ACCESSGRANTED_EVENT;
        final String eventSignature= EventEncoder.encode(event);
        String slaTopic= "0x" + serviceAgreementId;

        grantedFilter.addSingleTopic(eventSignature);
        grantedFilter.addOptionalTopics(slaTopic);


        return accessConditions.accessGrantedEventFlowable(grantedFilter);
    }

    public List<Condition> initializeConditions(String templateId, BaseManager.ContractAddresses addresses, Map<String, Object> params) throws InitializeConditionsException {

        try {
            params.putAll(getFunctionsFingerprints(templateId, addresses));

            if (conditionsTemplate == null)
                conditionsTemplate = new String(Files.readAllBytes(Paths.get(ACCESS_CONDITIONS_FILE_TEMPLATE)));

            params.forEach((_name, _func) -> {
                if (_func instanceof byte[])
                    conditionsTemplate = conditionsTemplate.replaceAll("\\{" + _name + "\\}", CryptoHelper.getHex((byte[]) _func));
                else
                    conditionsTemplate = conditionsTemplate.replaceAll("\\{" + _name + "\\}", _func.toString());
            });

            return AbstractModel
                    .getMapperInstance()
                    .readValue(conditionsTemplate, new TypeReference<List<Condition>>() {
                    });
        }catch (Exception e) {
            String msg = "Error initializing conditions for template: " +  templateId;
            log.error(msg);
            throw new InitializeConditionsException(msg, e);
        }
    }

    private static final String FUNCTION_LOCKPAYMENT_DEF= "lockPayment(bytes32,bytes32,uint256)";
    private static final String FUNCTION_GRANTACCESS_DEF= "grantAccess(bytes32,bytes32,bytes32)";
    private static final String FUNCTION_RELEASEPAYMENT_DEF= "releasePayment(bytes32,bytes32,uint256)";
    private static final String FUNCTION_REFUNDPAYMENT_DEF= "refundPayment(bytes32,bytes32,uint256)";


    /**
     * Compose the different conditionKey hashes using:
     * (serviceAgreementTemplateId, address, signature)
     * @return Map of (varible name => conditionKeys)
     */
    public Map<String, Object> getFunctionsFingerprints(String templateId, BaseManager.ContractAddresses addresses) throws UnsupportedEncodingException {


        String checksumPaymentConditionsAddress = Keys.toChecksumAddress(addresses.getPaymentConditionsAddress());
        String checksumAccessConditionsAddress = Keys.toChecksumAddress(addresses.getAccessConditionsAddres());

        Map<String, Object> fingerprints= new HashMap<>();
        fingerprints.put("function.lockPayment.fingerprint", EthereumHelper.getFunctionSelector(
                FUNCTION_LOCKPAYMENT_DEF));

        fingerprints.put("function.grantAccess.fingerprint", EthereumHelper.getFunctionSelector(
                FUNCTION_GRANTACCESS_DEF));

        fingerprints.put("function.releasePayment.fingerprint", EthereumHelper.getFunctionSelector(
                FUNCTION_RELEASEPAYMENT_DEF));

        fingerprints.put("function.refundPayment.fingerprint", EthereumHelper.getFunctionSelector(
                FUNCTION_REFUNDPAYMENT_DEF));

        fingerprints.put("function.lockPayment.conditionKey",
                fetchConditionKey(templateId, checksumPaymentConditionsAddress, EthereumHelper.getFunctionSelector(FUNCTION_LOCKPAYMENT_DEF)));

        fingerprints.put("function.grantAccess.conditionKey",
                fetchConditionKey(templateId, checksumAccessConditionsAddress, EthereumHelper.getFunctionSelector(FUNCTION_GRANTACCESS_DEF)));

        fingerprints.put("function.releasePayment.conditionKey",
                fetchConditionKey(templateId, checksumPaymentConditionsAddress, EthereumHelper.getFunctionSelector(FUNCTION_RELEASEPAYMENT_DEF)));

        fingerprints.put("function.refundPayment.conditionKey",
                fetchConditionKey(templateId, checksumPaymentConditionsAddress, EthereumHelper.getFunctionSelector(FUNCTION_REFUNDPAYMENT_DEF)));


        return fingerprints;
    }

    /**
     * Calculates the conditionKey
     * @param templateId
     * @param address Checksum address
     * @param fingerprint
     * @return
     */
    public static String fetchConditionKey(String templateId, String address, String fingerprint)   {

        templateId = templateId.replaceAll("0x", "");
        address = address.replaceAll("0x", "");
        fingerprint = fingerprint.replaceAll("0x", "");

        String params= templateId
                + address
                + fingerprint;

        return Hash.sha3(params);
    }





}
