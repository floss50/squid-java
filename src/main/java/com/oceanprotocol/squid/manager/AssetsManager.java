package com.oceanprotocol.squid.manager;

import com.oceanprotocol.squid.external.AquariusService;
import com.oceanprotocol.squid.external.KeeperService;
import com.oceanprotocol.squid.exceptions.DDOException;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.aquarius.SearchQuery;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import com.oceanprotocol.squid.models.service.MetadataService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.crypto.CipherException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Manages the functionality related with Assets
 */
public class AssetsManager extends BaseManager {

    static final Logger log= LogManager.getLogger(AssetsManager.class);

    public AssetsManager(KeeperService keeperService, AquariusService aquariusService)
            throws IOException, CipherException {
        super(keeperService, aquariusService);
    }

    /**
     * Gets an instance of AssetManager
     * @param keeperService the keeperService
     * @param aquariusService the aquarius service
     * @return an initialized instance of AssetManager
     * @throws IOException IOException
     * @throws CipherException CipherException
     */
    public static AssetsManager getInstance(KeeperService keeperService, AquariusService aquariusService)
            throws IOException, CipherException {
        return new AssetsManager(keeperService, aquariusService);
    }

    /**
     * Publishes in Aquarius the metadata of a DDO
     * @param ddo the ddo
     * @return the published DDO
     * @throws Exception Exception
     */
    public DDO publishMetadata(DDO ddo) throws Exception {
        return getAquariusService().createDDO(ddo);
    }

    /**
     * Publishes in Aquarius the metadata of a DDO
     * @param metadata the metadata
     * @param serviceEndpoint the endpoint
     * @return the published DDO
     * @throws Exception Exception
     */
    public DDO publishMetadata(AssetMetadata metadata, String serviceEndpoint) throws Exception {

        MetadataService service= new MetadataService(metadata, serviceEndpoint);

        return publishMetadata(
                this.buildDDO(service, null, getKeeperService().getAddress()));

    }

    /**
     * Gets a DDO from the DID
     * @param id the id
     * @return an instance of the DDO represented by the DID
     * @throws Exception Exception
     */
    public DDO getByDID(String id) throws Exception {
        return getAquariusService().getDDOUsingId(id);
    }

    /**
     * Updates the metadata of a DDO
     * @param id the id
     * @param ddo the ddo
     * @return A flag that indicates if the update was executed correctly
     * @throws Exception Exception
     */
    public boolean updateMetadata(String id, DDO ddo) throws Exception  {
        return getAquariusService().updateDDO(id, ddo);
    }

    /**
     * Gets all the DDOs that match the search criteria
     * @param text the criteria
     * @param offset parameter to paginate
     * @param page parameter to paginate
     * @return List of DDOs
     * @throws DDOException DDOException
     */
    public List<DDO> searchAssets(String text, int offset, int page) throws DDOException {
            return getAquariusService().searchDDO(text, offset, page);
    }

    /**
     * Gets all the DDOs that match the parameters of the query
     * @param params the criteria
     * @param offset parameter to paginate
     * @param page parameter to paginate
     * @param sort parameter to sort
     * @return a List with all the DDOs found
     * @throws DDOException DDOException
     */
    public List<DDO> searchAssets(Map<String, Object> params, int offset, int page, int sort) throws DDOException  {
        SearchQuery searchQuery= new SearchQuery(params, offset, page, sort);
        return getAquariusService().searchDDO(searchQuery);
    }


}
