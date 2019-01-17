package com.oceanprotocol.squid.models.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.oceanprotocol.squid.helpers.EncodingHelper;
import com.oceanprotocol.squid.helpers.EthereumHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder(alphabetic=true)
public class AccessService extends Service {

    private static final Logger log = LogManager.getLogger(AccessService.class);

    @JsonProperty
    public String purchaseEndpoint;

    @JsonProperty
    public List<Condition> conditions= new ArrayList<>();

    @JsonProperty
    public ServiceAgreementContract serviceAgreementContract;

    @JsonPropertyOrder(alphabetic=true)
    public static class ServiceAgreementContract {

        @JsonProperty
        public String contractName;

        @JsonProperty
        public int fulfillmentOperator;

        @JsonProperty
        public List<Condition.Event> events= new ArrayList<>();

        public ServiceAgreementContract() {}
    }

    public AccessService(String serviceEndpoint, String serviceDefinitionId)  {
        super(serviceTypes.Access, serviceEndpoint, serviceDefinitionId);
        this.templateId= ACCESS_TEMPLATE_ID;
    }

    public AccessService(String serviceEndpoint, String serviceDefinitionId,
                         ServiceAgreementContract serviceAgreementContract
    )  {
        super(serviceTypes.Access, serviceEndpoint, serviceDefinitionId);
        this.templateId= ACCESS_TEMPLATE_ID;
        this.serviceAgreementContract = serviceAgreementContract;

    }

    /**
     * Generates a Hash representing the Access Service Agreement
     * The Hash is having the following parameters:
     * (templateId, conditionKeys, conditionValues, timeout, serviceAgreementId)
     * @param serviceAgreementId Service Agreement Id
     * @return Hash
     */
    public String generateServiceAgreementHash(String serviceAgreementId) throws IOException  {
        String params=
                    templateId
                    + fetchConditionKeys()
                    + fetchConditionValues()
                    + fetchTimeout()
                    + serviceAgreementId;

        return Hash.sha3(EthereumHelper.remove0x(params));
    }

    public String generateServiceAgreementSignatureFromHash(Web3j web3, String address, String hash) throws IOException {
        return EthereumHelper.ethSignMessage(web3, hash, address);
    }

    public String generateServiceAgreementSignature(Web3j web3, String address, String serviceAgreementId) throws IOException {
        String hash= generateServiceAgreementHash(serviceAgreementId);
        return EthereumHelper.ethSignMessage(web3, hash, address);
    }

    public String fetchConditionKeys()  {
        String conditionKeys= "";

        for (Condition condition: conditions)   {
            conditionKeys= conditionKeys + EthereumHelper.remove0x(condition.conditionKey);
        }
        return conditionKeys;
    }

    public String fetchConditionValues() throws UnsupportedEncodingException {
        String data= "";

        for (Condition condition: conditions)   {
            String token= "";
            for (Condition.ConditionParameter param: condition.parameters) {

                if (param.type.equals("string"))
                    token= token + EthereumHelper.remove0x((String) param.value);
                else if (param.type.contains("bytes32"))
                    token= token + EthereumHelper.remove0x((String) param.value);
                else if (param.type.contains("int"))
                  if (param.value instanceof String)
                        token= token + EthereumHelper.remove0x(EncodingHelper.hexEncodeAbiType("uint", Integer.parseInt((String)param.value)));
                else
                      token= token + EthereumHelper.remove0x(EncodingHelper.hexEncodeAbiType("uint", param.value));

            }
            data= data + EthereumHelper.remove0x(Hash.sha3(token));
        }

        return data;
    }

    public String fetchTimeout() throws IOException {
        String data= "";

        for (Condition condition: conditions)   {
            data= data + EthereumHelper.remove0x(
                    EncodingHelper.hexEncodeAbiType("uint256", condition.timeout));
        }

        return data;
    }

}
