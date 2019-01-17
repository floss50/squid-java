package com.oceanprotocol.squid.manager;

import com.oceanprotocol.squid.core.sla.AccessSLA;
import com.oceanprotocol.squid.core.sla.SlaManager;
import com.oceanprotocol.squid.dto.AquariusDto;
import com.oceanprotocol.squid.dto.BrizoDto;
import com.oceanprotocol.squid.dto.KeeperDto;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.DID;
import com.oceanprotocol.squid.models.aquarius.SearchQuery;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import com.oceanprotocol.squid.models.brizo.InitializeAccessSLA;
import com.oceanprotocol.squid.models.service.AccessService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.crypto.CipherException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssetsController extends BaseController {

    static final Logger log= LogManager.getLogger(AssetsController.class);

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
        return getAquariusDto().getDDOUsingId(id);
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
