package com.oceanprotocol.squid.manager;

import com.oceanprotocol.squid.dto.KeeperDto;
import com.oceanprotocol.squid.dto.ProviderDto;
import com.oceanprotocol.squid.models.asset.Asset;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.DID;
import com.oceanprotocol.squid.models.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.crypto.CipherException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OceanController extends BaseController {

    static final Logger log= LogManager.getLogger(OceanController.class);

    protected OceanController(KeeperDto keeperDto, ProviderDto providerDto)
            throws IOException, CipherException {
        super(keeperDto, providerDto);
    }

    /**
     * Given the KeeperDto and ProviderDto, returns a new instance of OceanController
     * using them as attributes
     * @param keeperDto Keeper Dto
     * @param providerDto Provider Dto
     * @return OceanController
     */
    public static OceanController getInstance(KeeperDto keeperDto, ProviderDto providerDto)
            throws IOException, CipherException {
        return new OceanController(keeperDto, providerDto);
    }


    public List<AssetMetadata> searchAssets()   {
        return new ArrayList<>();
    }

    public List<AssetMetadata> searchOrders()   {
        return new ArrayList<>();
    }

    public AssetMetadata register()   {
        return null;
    }

    public Asset resolveDID(DID did) {
        return null;
    }

    public DID generateDID(DDO ddo) throws DID.DIDFormatException {
        return new DID(DID.PREFIX + "123");
    }

    public Asset resolveDDO(DID did)    {
        return null;
    }

    public Order getOrder(String orderId)   {
        return null;
    }
}
