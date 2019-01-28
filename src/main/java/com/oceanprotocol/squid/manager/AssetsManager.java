package com.oceanprotocol.squid.manager;

import com.oceanprotocol.squid.external.AquariusService;
import com.oceanprotocol.squid.external.KeeperService;
import com.oceanprotocol.squid.exceptions.DDOException;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.aquarius.SearchQuery;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.crypto.CipherException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AssetsManager extends BaseManager {

    static final Logger log= LogManager.getLogger(AssetsManager.class);

    public AssetsManager(KeeperService keeperService, AquariusService aquariusService)
            throws IOException, CipherException {
        super(keeperService, aquariusService);
    }

    public static AssetsManager getInstance(KeeperService keeperService, AquariusService aquariusService)
            throws IOException, CipherException {
        return new AssetsManager(keeperService, aquariusService);
    }

    public DDO publishMetadata(DDO ddo) throws Exception {
        return getAquariusService().createDDO(ddo);
    }

    public DDO publishMetadata(AssetMetadata metadata, String serviceEndpoint) throws Exception {

        return publishMetadata(
                new DDO(metadata, getKeeperService().getAddress(), serviceEndpoint));

    }

    public DDO getByDID(String id) throws Exception {
        return getAquariusService().getDDOUsingId(id);
    }

    public boolean updateMetadata(String id, DDO ddo) throws Exception  {
        return getAquariusService().updateDDO(id, ddo);
    }

    public List<DDO> searchAssets(String text, int offset, int page) throws DDOException {
            return getAquariusService().searchDDO(text, offset, page);
    }

    public List<DDO> searchAssets(Map<String, Object> params, int offset, int page, int sort) throws DDOException  {
        SearchQuery searchQuery= new SearchQuery(params, offset, page, sort);
        return getAquariusService().searchDDO(searchQuery);
    }


}
