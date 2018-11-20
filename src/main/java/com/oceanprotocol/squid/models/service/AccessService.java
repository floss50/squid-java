package com.oceanprotocol.squid.models.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.oceanprotocol.squid.helpers.EncodingHelper;
import org.web3j.crypto.Hash;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.oceanprotocol.squid.helpers.CryptoHelper.soliditySha3;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder(alphabetic=true)
public class AccessService extends Service {

    @JsonProperty
    public String purchaseEndpoint;

    @JsonProperty
    public List<Condition> conditions= new ArrayList<>();

    public ServiceAgreementContract serviceAgreementContract;

    @JsonIgnoreProperties(ignoreUnknown = true)
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



    public AccessService() {
        this.type= serviceTypes.Access.toString();
    }

    public AccessService(String serviceEndpoint, String serviceDefinitionId)  {
        super(serviceTypes.Access, serviceEndpoint, serviceDefinitionId);
        this.templateId= ACCESS_TEMPLATE_ID;
    }

    /**
     * Generates a Hash representing the Access Service Agreement
     * The Hash is having the following parameters:
     * (serviceAgreementId, templateId, conditionKeys, timeouts, conditionParameters)
     * @param serviceAgreementId Service Agreement Id
     * @return Hash
     */
    public byte[] getServiceAgreementHash(String serviceAgreementId) throws IOException  {
        return soliditySha3(
                EncodingHelper.hexStringToBytes(serviceAgreementId),
                this.templateId,
                getConditionKeysHash(),
                getConditionValuesHash()
                );
    }

    public byte[] getConditionKeysHash() throws IOException {

        byte[] templateIdHex= EncodingHelper.hexStringToBytes(templateId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (Condition condition: conditions)   {
            baos.write(soliditySha3(
                    templateIdHex,
                    EncodingHelper.hexStringToBytes(condition.conditionKey),
                    EncodingHelper.stringToBytes(condition.contractName)
            ));
        }
        return baos.toByteArray();
    }

    public byte[] getConditionValuesHash() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (Condition condition: conditions)   {
            for (Condition.ConditionParameter param: condition.parameters) {
                if (param.type.equals("string"))
                    baos.write(soliditySha3(
                            EncodingHelper.stringToBytes(param.type), EncodingHelper.stringToBytes((String)param.value)));
                if (param.type.equals("bytes32"))
                    baos.write(soliditySha3(
                            EncodingHelper.stringToBytes(param.type), EncodingHelper.hexStringToBytes((String)param.value)));
                else
                    baos.write(soliditySha3(
                            EncodingHelper.stringToBytes(param.type), param.value));
            }
        }
        return baos.toByteArray();
    }


}
