package com.oceanprotocol.squid.models.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.oceanprotocol.squid.helpers.EncodingHelper;
import com.oceanprotocol.squid.helpers.EthereumHelper;
import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
     * (templateId, conditionKeys, conditionValues, timeout, serviceAgreementId)
     * @param serviceAgreementId Service Agreement Id
     * @return Hash
     */
    public String generateServiceAgreementHash(String serviceAgreementId) throws IOException  {
        String params=
            EthereumHelper.remove0x(templateId)
                    + fetchConditionKeys(conditions)
                    + fetchConditionValues(conditions)
                    + fetchTimeout()
                    + EthereumHelper.remove0x(serviceAgreementId);

        //return EthereumHelper.remove0x(Hash.sha3(params));
        return Hash.sha3(params);
    }

    public String generateServiceAgreementSignature(Web3j web3, String address, String serviceAgreementId) throws IOException {
        String hash= generateServiceAgreementHash(serviceAgreementId);
        return EthereumHelper.ethEncodeAndSignMessage(web3, hash, address);
    }

    /**
     * return web3.soliditySha3(
     *      ['bytes32', 'bytes32[]', 'bytes32[]', 'uint256[]', 'bytes32'],
     *      [sa_template_id, condition_keys, values_hash_list, timeouts, service_agreement_id]
     */

    public String fetchConditionKeys(List<Condition> conditions)  {
        String conditionKeys= "";

        int counter= 0;
        for (Condition condition: conditions)   {
            conditionKeys= conditionKeys + EthereumHelper.remove0x(condition.conditionKey);
            counter++;
        }
        return conditionKeys;
    }


    public String fetchConditionValues(List<Condition> conditions) throws UnsupportedEncodingException {
        String data= "";

        int counter= 0;
        for (Condition condition: conditions)   {
            for (Condition.ConditionParameter param: condition.parameters) {

                if (param.type.equals("string"))
                    data= data + EthereumHelper.remove0x((String) param.value);
                else if (param.type.contains("bytes32"))
                    data= data + EncodingHelper.hexEncodeAbiType("bytes32", param.value);
                else if (param.type.startsWith("uint"))
                    data= data + EncodingHelper.hexEncodeAbiType("uint", param.value);
                else
                    data= data + EthereumHelper.remove0x((String) param.value);

                counter++;
            }
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

/*
    public byte[][] fetchConditionKeysHash() throws IOException {

        byte[][] data= new byte[conditions.size()*3][];
        int counter= 0;
        byte[] templateIdHex= EncodingHelper.hexStringToBytes(templateId);

        for (Condition condition: conditions)   {
            data[counter] = templateIdHex;
            counter++;
            data[counter]= EncodingHelper.hexStringToBytes(condition.conditionKey);
            counter++;
            data[counter]= EncodingHelper.stringToBytes(condition.contractName);
            counter++;
        }


        return data;
    }

    public byte[][] fetchTimeoutByte() throws IOException {
        byte[][] data= new byte[conditions.size()][];
        int counter= 0;

        for (Condition condition: conditions)   {
            data[counter]= ByteBuffer.allocate(Integer.BYTES).putInt(condition.timeout).array();
            counter++;
        }

        return data;
    }

    public byte[] fetchTimeoutHash() throws IOException {
        List<byte[]> hashList= new ArrayList<>();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (Condition condition: conditions)   {
            baos.write(soliditySha3(condition.timeout));
        }
        return baos.toByteArray();
    }



    public byte[][] fetchConditionValuesBytes() throws IOException {
        AtomicInteger numItems= new AtomicInteger(0);
        conditions.forEach(c -> {numItems.addAndGet(c.parameters.size());});
        byte[][] data= new byte[numItems.get() * 2][];

        int counter= 0;

        for (Condition condition: conditions)   {
            for (Condition.ConditionParameter param: condition.parameters) {
                data[counter] = EncodingHelper.stringToBytes(param.type);
                counter++;

                if (param.type.equals("string"))
                    data[counter]= EncodingHelper.stringToBytes((String)param.value);
                if (param.type.equals("bytes32"))
                    data[counter]= EncodingHelper.hexStringToBytes((String)param.value);
                if (param.type.startsWith("uint"))
                    data[counter]= EncodingHelper.integerToBytes((int)param.value);
                else
                    data[counter]= EncodingHelper.stringToBytes((String)param.value);

                counter++;
            }
        }
        return data;
    }

    public byte[] fetchConditionValuesHash() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (Condition condition: conditions)   {
            for (Condition.ConditionParameter param: condition.parameters) {
                if (param.type.equals("string"))
                    baos.write(soliditySha3(
                            EncodingHelper.stringToBytes(param.type), EncodingHelper.stringToBytes((String)param.value)));
                if (param.type.equals("bytes32"))
                    baos.write(soliditySha3(
                            EncodingHelper.stringToBytes(param.type), EncodingHelper.hexStringToBytes((String)param.value)));
                if (param.type.startsWith("uint"))
                    baos.write(soliditySha3(EncodingHelper.stringToBytes(param.type), EncodingHelper.integerToBytes((int)param.value)));
                else
                    baos.write(soliditySha3(
                            EncodingHelper.stringToBytes(param.type), param.value));
            }
        }
        return baos.toByteArray();
    }*/


}
