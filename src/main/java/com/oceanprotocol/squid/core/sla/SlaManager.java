package com.oceanprotocol.squid.core.sla;

import com.oceanprotocol.squid.models.service.Service;

import java.util.UUID;

public class SlaManager {

    public SlaManager() {
    }

    public static String generateSlaId()    {
        String token= UUID.randomUUID().toString() + UUID.randomUUID().toString();
        return token.replaceAll("-", "");
    }


}
