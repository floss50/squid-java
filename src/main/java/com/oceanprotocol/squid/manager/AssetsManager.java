package com.oceanprotocol.squid.manager;

import com.oceanprotocol.squid.dto.AquariusDto;
import com.oceanprotocol.squid.dto.KeeperDto;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.aquarius.SearchQuery;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.crypto.CipherException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssetsManager extends BaseManager {

    static final Logger log= LogManager.getLogger(AssetsManager.class);

    public AssetsManager(KeeperDto keeperDto, AquariusDto aquariusDto)
            throws IOException, CipherException {
        super(keeperDto, aquariusDto);
    }

    public static AssetsManager getInstance(KeeperDto keeperDto, AquariusDto aquariusDto)
            throws IOException, CipherException {
        return new AssetsManager(keeperDto, aquariusDto);
    }

    public DDO publishMetadata(DDO ddo) throws Exception {
        return getAquariusDto().createDDO(ddo);
    }

    public DDO publishMetadata(AssetMetadata metadata, String serviceEndpoint) throws Exception {

        return publishMetadata(
                new DDO(metadata, getKeeperDto().getAddress(), serviceEndpoint));

    }

    public DDO getByDID(String id) throws Exception {
        return getAquariusDto().getDDOUsingId(id);
    }

    public boolean updateMetadata(String id, DDO ddo) throws Exception  {
        return getAquariusDto().updateDDO(id, ddo);
    }

    public List<DDO> searchAssets(String text, int offset, int page)   {
        try {
            return getAquariusDto().searchDDO(text, offset, page);
        } catch (Exception ex)  {
            log.error("Error searching for DDO's " + ex.getMessage());
        }
        return new ArrayList<>();
    }

    public List<DDO> searchAssets(Map<String, Object> params, int offset, int page, int sort)   {
        SearchQuery searchQuery= new SearchQuery(params, offset, page, sort);

        try {
            return getAquariusDto().searchDDO(searchQuery);
        } catch (Exception ex)  {
            log.error("Error searching for DDO's " + ex.getMessage());
        }
        return new ArrayList<>();
    }


}
