package com.oceanprotocol.squid.manager;

import com.oceanprotocol.squid.dto.AquariusDto;
import com.oceanprotocol.squid.dto.KeeperDto;
import com.oceanprotocol.squid.helpers.EncodingHelper;
import com.oceanprotocol.squid.helpers.UrlHelper;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.DID;
import com.oceanprotocol.squid.models.Order;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class OceanController extends BaseController {

    static final Logger log= LogManager.getLogger(OceanController.class);

    static final BigInteger DID_VALUE_TYPE= BigInteger.valueOf(0);


    protected OceanController(KeeperDto keeperDto, AquariusDto aquariusDto)
            throws IOException, CipherException {
        super(keeperDto, aquariusDto);
    }

    /**
     * Given the KeeperDto and AquariusDto, returns a new instance of OceanController
     * using them as attributes
     * @param keeperDto Keeper Dto
     * @param aquariusDto Provider Dto
     * @return OceanController
     */
    public static OceanController getInstance(KeeperDto keeperDto, AquariusDto aquariusDto)
            throws IOException, CipherException {
        return new OceanController(keeperDto, aquariusDto);
    }

    public DID generateDID(DDO ddo) throws DID.DIDFormatException {
        return DID.builder();
    }

    public DDO resolveDID(DID did) throws IOException {

        EthFilter didFilter = new EthFilter(
                DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST,
                didRegistry.getContractAddress()
        );

        final Event event= didRegistry.DIDATTRIBUTEREGISTERED_EVENT;
        final String eventSignature= EventEncoder.encode(event);
        didFilter.addSingleTopic(eventSignature);

        //String didTopic= "0x" + EncodingHelper.encodeToHex(did.getHash());
        String didTopic= "0x" + did.getHash();
        String metadataTopic= "0x" + EncodingHelper.padRightWithZero(
                EncodingHelper.encodeToHex(DDO.Service.serviceTypes.Metadata.toString()), 64);

        didFilter.addOptionalTopics(didTopic, metadataTopic);

        EthLog ethLog= getKeeperDto().getWeb3().ethGetLogs(didFilter).send();

        List<EthLog.LogResult> logs = ethLog.getLogs();

        int numLogs= logs.size();
        if (numLogs<1)
            throw new IOException("No events found for " + did.toString());

        try {
            EthLog.LogResult logResult= logs.get(numLogs-1);
            List<Type> nonIndexed= FunctionReturnDecoder.decode(((EthLog.LogObject) logResult).getData(), event.getNonIndexedParameters());
            String ddoUrl= nonIndexed.get(0).getValue().toString();
            String didUrl= UrlHelper.parseDDOUrl(ddoUrl, did.toString());

            AquariusDto ddoAquariosDto= AquariusDto.getInstance(UrlHelper.getBaseUrl(didUrl));
            return ddoAquariosDto.getDDO(didUrl);

        } catch (URISyntaxException ex) {
            log.error("Error parsing url " + ex.getMessage());
            throw new IOException("Error parsing url  " + ex.getMessage());

        } catch (Exception ex)  {
            log.error("Unable to retrieve DDO " + ex.getMessage());
            throw new IOException("Unable to retrieve DDO " + ex.getMessage());
        }
    }




    public boolean registerDID(DID did, String url) throws Exception{
        log.debug("Registering DID " + did.getHash() + "into Registry " + didRegistry.getContractAddress());

        TransactionReceipt receipt= didRegistry.registerAttribute(
                EncodingHelper.hexStringToBytes(did.getHash()),
                DID_VALUE_TYPE,
                EncodingHelper.byteArrayToByteArray32(EncodingHelper.stringToBytes(DDO.Service.serviceTypes.Metadata.toString())),
                url
        ).send();

        if (!receipt.getStatus().equals("0x1"))
            return false;
        return true;
    }

    // TODO: to be implemented
    public Order getOrder(String orderId)   {
        return null;
    }

    // TODO: to be implemented
    public List<AssetMetadata> searchAssets()   {
        return new ArrayList<>();
    }

    // TODO: to be implemented
    public List<AssetMetadata> searchOrders()   {
        return new ArrayList<>();
    }

    // TODO: to be implemented
    public AssetMetadata register()   {
        return null;
    }


}
