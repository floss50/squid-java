package com.oceanprotocol.squid.core.sla;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oceanprotocol.squid.helpers.EthereumHelper;
import com.oceanprotocol.squid.models.AbstractModel;
import com.oceanprotocol.squid.models.service.Condition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessSLA implements SlaFunctions {

    private static final String ACCESS_CONDITIONS_FILE_TEMPLATE= "src/main/resources/sla/sla-access-conditions-template.json";
    private String conditionsTemplate= null;

    public List<Condition> initializeConditions(Map<String, Object> params) throws IOException {
        params.putAll(getFunctionFingerprints());

        if (conditionsTemplate == null)
            conditionsTemplate = new String(Files.readAllBytes(Paths.get(ACCESS_CONDITIONS_FILE_TEMPLATE)));

        params.forEach( (_name, _func) -> {
            conditionsTemplate= conditionsTemplate.replaceAll("\\{" + _name + "\\}", _func.toString());
        });

        return AbstractModel
                .getMapperInstance()
                .readValue(conditionsTemplate, new TypeReference<List<Condition>>() {});
    }

    public Map<String, Object> getFunctionFingerprints()    {
        Map<String, Object> fingerprints= new HashMap<>();
        fingerprints.put("function.lockPayment.fingerprint", EthereumHelper.getFunctionSelector(
                "lockPayment(bytes32,uint)"));

        fingerprints.put("function.grantAccess.fingerprint", EthereumHelper.getFunctionSelector(
                "grantAccess(bytes32,bytes32)"));

        fingerprints.put("function.releasePayment.fingerprint", EthereumHelper.getFunctionSelector(
                "releasePayment(bytes32,uint)"));

        fingerprints.put("function.refundPayment.fingerprint", EthereumHelper.getFunctionSelector(
                "lockPayment(bytes32,uint)"));


        return fingerprints;
    }

}
