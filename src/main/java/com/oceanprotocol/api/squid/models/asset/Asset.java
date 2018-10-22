package com.oceanprotocol.api.squid.models.asset;

import com.oceanprotocol.api.squid.models.DDO;

public class Asset extends DDO {

    public String getDID()  {
        return id.toString();
    }
}
