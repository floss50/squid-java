package com.oceanprotocol.squid.manager;

import com.oceanprotocol.squid.dto.KeeperDto;
import com.oceanprotocol.squid.dto.AquariusDto;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.DID;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import org.web3j.crypto.CipherException;

import java.io.IOException;

public class AssetsController extends BaseController {

    public AssetsController(KeeperDto keeperDto, AquariusDto aquariusDto)
            throws IOException, CipherException {
        super(keeperDto, aquariusDto);
    }

    public static AssetsController getInstance(KeeperDto keeperDto, AquariusDto aquariusDto)
            throws IOException, CipherException {
        return new AssetsController(keeperDto, aquariusDto);
    }

    public DDO publishMetadata(DDO ddo) throws Exception {
        return getAquariusDto().createDDO(ddo);
    }

    public DDO publishMetadata(AssetMetadata metadata, String serviceEndpoint) throws Exception {

        return publishMetadata(
                new DDO(metadata, getKeeperDto().getAddress(), serviceEndpoint));

    }

    public DDO getByDID(DID did) throws Exception   {
        return getByDID(did.toString());
    }

    public DDO getByDID(String id) throws Exception {
        return getAquariusDto().getDDO(id);
    }

    public DDO getByDIDUsingUrl(String url) throws Exception {
        return getAquariusDto().getDDO(url);
    }

    public DDO getMetadata(String id) throws Exception {
        return getByDID(id);
    }

    public boolean updateMetadata(String id, DDO ddo) throws Exception  {
        return getAquariusDto().updateDDO(id, ddo);
    }

}
