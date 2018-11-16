package com.oceanprotocol.squid.models.asset;

import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.DID;

public class Asset extends DDO {


    public Asset() throws DID.DIDFormatException {
    }

    public String getId() { return getDid().toString();}


    public DDO getDDO() { return this;}
}
