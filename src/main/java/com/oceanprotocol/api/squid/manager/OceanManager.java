package com.oceanprotocol.api.squid.manager;

import com.oceanprotocol.api.squid.dto.KeeperDto;
import com.oceanprotocol.api.squid.dto.ProviderDto;
import com.oceanprotocol.api.squid.models.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.core.methods.response.EthAccounts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OceanManager extends BaseManager {

    static final Logger log= LogManager.getLogger(OceanManager.class);

    protected OceanManager(KeeperDto keeperDto, ProviderDto providerDto)
            throws IOException, CipherException {
        super(keeperDto, providerDto);
    }

    /**
     * Given the KeeperDto and ProviderDto, returns a new instance of OceanManager
     * using them as attributes
     * @param keeperDto Keeper Dto
     * @param providerDto Provider Dto
     * @return OceanManager
     */
    public static OceanManager getInstance(KeeperDto keeperDto, ProviderDto providerDto)
            throws IOException, CipherException {
        return new OceanManager(keeperDto, providerDto);
    }


    public List<Asset> searchAssets()   {
        return new ArrayList<>();
    }

    public List<Asset> searchOrders()   {
        return new ArrayList<>();
    }

    public Asset register()   {
        return null;
    }

    public DID generateDID() throws DID.DIDFormatException {
        return new DID(DID.PREFIX + "123");
    }

    public Asset resolveDDO(DID did)    {
        return null;
    }

    public Order getOrder(String orderId)   {
        return null;
    }
}
