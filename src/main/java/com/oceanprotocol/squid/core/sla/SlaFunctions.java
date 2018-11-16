package com.oceanprotocol.squid.core.sla;

import com.oceanprotocol.squid.models.service.Condition;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SlaFunctions {

    List<Condition> initializeConditions(Map<String, Object> params) throws IOException;

    Map<String, Object> getFunctionFingerprints();
}
