package com.oceanprotocol.squid.core.sla;

import com.oceanprotocol.squid.exceptions.InitializeConditionsException;
import com.oceanprotocol.squid.manager.BaseManager;
import com.oceanprotocol.squid.models.service.Condition;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface SlaFunctions {

    List<Condition> initializeConditions(String templateId, BaseManager.ContractAddresses addresses, Map<String, Object> params) throws InitializeConditionsException;

    Map<String, Object> getFunctionsFingerprints(String templateId, BaseManager.ContractAddresses addresses) throws UnsupportedEncodingException;
}
