package com.oceanprotocol.squid.manager;

import com.oceanprotocol.keeper.contracts.AccessConditions;
import com.oceanprotocol.keeper.contracts.ServiceAgreement;
import com.oceanprotocol.squid.core.sla.AccessSLA;
import com.oceanprotocol.squid.core.sla.SlaManager;
import com.oceanprotocol.squid.core.sla.func.LockPayment;
import com.oceanprotocol.squid.dto.AquariusDto;
import com.oceanprotocol.squid.dto.BrizoDto;
import com.oceanprotocol.squid.dto.KeeperDto;
import com.oceanprotocol.squid.helpers.EncodingHelper;
import com.oceanprotocol.squid.helpers.StringsHelper;
import com.oceanprotocol.squid.helpers.UrlHelper;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.DID;
import com.oceanprotocol.squid.models.Order;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import com.oceanprotocol.squid.models.asset.BasicAssetInfo;
import com.oceanprotocol.squid.models.brizo.InitializeAccessSLA;
import com.oceanprotocol.squid.models.service.*;
import io.reactivex.Flowable;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Keys;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;


public class OceanController extends BaseController {

    static final Logger log= LogManager.getLogger(OceanController.class);

    // TODO  Check the business logic in squid-py related with this DID Types !!!
    static final BigInteger DID_VALUE_TYPE= BigInteger.valueOf(2);


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

    /**
     * Given a DDO, returns a DID created using the ddo
     * @param ddo
     * @return DID
     * @throws DID.DIDFormatException
     */
    public DID generateDID(DDO ddo) throws DID.DIDFormatException {
        return DID.builder();
    }

    /**
     * Given a DID, scans the DIDRegistry events on-chain to resolve the
     * Metadata API url and return the DDO found
     * @param did
     * @return DDO
     * @throws IOException
     */
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
                EncodingHelper.encodeToHex(Service.serviceTypes.Metadata.toString()), 64);

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


    /**
     * Given a DID and a Metadata API url, register on-chain the DID.
     * It allows to resolve DDO's using DID's as input
     * @param did
     * @param url metadata url
     * @return boolean success
     * @throws Exception
     */
    public boolean registerDID(DID did, String url) throws Exception{
        log.debug("Registering DID " + did.getHash() + " into Registry " + didRegistry.getContractAddress());

        TransactionReceipt receipt= didRegistry.registerAttribute(
                EncodingHelper.hexStringToBytes(did.getHash()),
                DID_VALUE_TYPE,
                EncodingHelper.byteArrayToByteArray32(EncodingHelper.stringToBytes(Service.serviceTypes.Metadata.toString())),
                url
        ).send();

        if (!receipt.getStatus().equals("0x1"))
            return false;
        return true;
    }

    public DDO registerAsset(AssetMetadata metadata, String address, Endpoints serviceEndpoints, int threshold) throws Exception {

        // Initializing DDO
        DDO ddo= new DDO(address);

        // Encrypting contentUrls and adding them to the Metadata
        ArrayList<String> urls= new ArrayList<>();
        urls.add(encryptContentUrls(ddo.getDid(), metadata.base.contentUrls, threshold));
        metadata.base.contentUrls= urls;

        // Definition of service endpoints

        String metadataEndpoint;
        if (serviceEndpoints.getMetadataEndpoint() == null)
            metadataEndpoint= getAquariusDto().getDdoEndpoint() + "/{did}";
        else
            metadataEndpoint= serviceEndpoints.getMetadataEndpoint();


        // Initialization of services supported for this asset
        MetadataService metadataService= new MetadataService(metadata, metadataEndpoint, "0");


        // Definition of a DEFAULT ServiceAgreement Contract
        AccessService.ServiceAgreementContract serviceAgreementContract = new AccessService.ServiceAgreementContract();
        serviceAgreementContract.contractName = "ServiceAgreement";
        serviceAgreementContract.fulfillmentOperator = 1;

        // Execute Agreement Event
        Condition.Event executeAgreementEvent = new Condition.Event();
        executeAgreementEvent.name = "ExecuteAgreement";
        executeAgreementEvent.actorType = "consumer";
        // Handler
        Condition.Handler handler = new Condition.Handler();
        handler.moduleName = "payment";
        handler.functionName = "lockPayment";
        handler.version = "0.1";
        executeAgreementEvent.handler = handler;

        serviceAgreementContract.events = Arrays.asList(executeAgreementEvent);


        AccessService accessService= new AccessService(serviceEndpoints.getAccessEndpoint(),
                "1",
                serviceAgreementContract);


        accessService.purchaseEndpoint= serviceEndpoints.getPurchaseEndpoint();

        // Initializing conditions and adding to Access service
        AccessSLA sla= new AccessSLA();
        accessService.conditions= sla.initializeConditions(
                accessService.templateId,
                getContractAddresses(),
                getAccessConditionParams(ddo.getDid().toString(), Integer.parseInt(metadata.base.price)));

        // Adding services to DDO
        ddo.addService(metadataService)
                .addService(accessService);

        // Storing DDO
        DDO createdDDO= getAquariusDto().createDDO(ddo);

        // Registering DID
        registerDID(ddo.getDid(), metadataEndpoint);

        return createdDDO;
    }

    public AccessSLA.SLAResponse initializePurchaseAsset(DID did, String serviceDefinitionId, String address) throws IOException {

        DDO ddo;

        // 1. Generate Service Agreement Id
        String serviceAgreementId= SlaManager.generateSlaId();

        // Checking if DDO is already there and serviceDefinitionId is included
        try {

            ddo= resolveDID(did);
        } catch (IOException e) {
            log.error("Error resolving did[" + did.getHash() + "]: " + e.getMessage());
            throw new IOException(e.getMessage());
        }

        AccessService accessService= ddo.getAccessService(serviceDefinitionId);

        // 2. Consumer sign service details. It includes:
        // (templateId, conditionKeys, valuesHashList, timeoutValues, serviceAgreementId)
        String agreementSignature= accessService.generateServiceAgreementSignature(
                getKeeperDto().getWeb3(),
                address,
                serviceAgreementId
                );

        InitializeAccessSLA initializePayload= new InitializeAccessSLA(
                did.toString(),
                "0x".concat(serviceAgreementId),
                serviceDefinitionId,
                agreementSignature,
                Keys.toChecksumAddress(address)
        );

        // 3. Send agreement details to Publisher (Brizo endpoint)
        boolean isInitialized= BrizoDto.initializeAccessServiceAgreement(accessService.purchaseEndpoint, initializePayload);

        if (!isInitialized)  {
            throw new IOException("Unable to initialize SLA using Brizo");
        }


        // 4. Listening of events
       return  new AccessSLA.SLAResponse(AccessSLA.listenExecuteAgreement(serviceAgreement, serviceAgreementId),
               serviceAgreementId);

    }


    public void lockPayment(String serviceDefinitionId, Flowable<ServiceAgreement.ExecuteAgreementEventResponse> flowable) {

        flowable.subscribe(event -> {

            if (event.state) {

                String serviceAgreementId =  EncodingHelper.toHexString(event.serviceAgreementId);
                log.debug("Receiving event - " + EncodingHelper.toHexString(event.serviceAgreementId));

                DID did =  new DID(EncodingHelper.toHexString(event.did));
                DDO ddo;

                try {

                    ddo = resolveDID(did);

                } catch (IOException e) {
                    log.error("Error resolving did[" + did.getHash() + "]: " + e.getMessage());
                    throw new IOException(e.getMessage());
                }

                AccessService accessService= ddo.getAccessService(serviceDefinitionId);
                BasicAssetInfo assetInfo = getBasicAssetInfo(accessService);

                LockPayment.executeLockPayment(paymentConditions, serviceAgreementId, ddo, assetInfo);

            }
        });

    }


    public boolean lockPayment(DID did, String serviceDefinitionId, String serviceAgreementId) throws IOException {

        DDO ddo;

        try {

            ddo = resolveDID(did);

        } catch (IOException e) {
            log.error("Error resolving did[" + did.getHash() + "]: " + e.getMessage());
            throw new IOException(e.getMessage());
        }

        AccessService accessService= ddo.getAccessService(serviceDefinitionId);
        BasicAssetInfo assetInfo = getBasicAssetInfo(accessService);

        return LockPayment.executeLockPayment(paymentConditions, serviceAgreementId, ddo, assetInfo);


    }

    public Flowable<AccessConditions.AccessGrantedEventResponse> listenForGrantedAccess(AccessConditions accessConditions,
                                                                                        String serviceAgreementId)
    {
        return AccessSLA.listenForGrantedAccess(accessConditions, serviceAgreementId);

    }


    public void consume(String serviceDefinitionId, String serviceAgreementId, DID did, String consumerAddress, String basePath) throws IOException {

        consume(serviceDefinitionId, serviceAgreementId, did, consumerAddress, basePath,0);
    }


    public boolean consume(String serviceDefinitionId, String serviceAgreementId, DID did, String consumerAddress, String basePath, int threshold) throws IOException {

        DDO ddo;

        try {

            ddo = resolveDID(did);
        } catch (IOException e) {
            log.error("Error resolving did[" + did.getHash() + "]: " + e.getMessage());
            throw new IOException(e.getMessage());
        }

        String serviceEndpoint = ddo.getAccessService(serviceDefinitionId).serviceEndpoint;
        List<String> contentUrls = StringsHelper.getStringsFromJoin( decryptContentUrls(did,  ddo.metadata.base.contentUrls.get(0), threshold));

        for (String url: contentUrls) {

            // For each url we call to consume Brizo endpoint that requires consumerAddress, serviceAgreementId and url as a parameters
            try {

                String fileName = url.substring(url.lastIndexOf("/"));

                InputStream dataStream = BrizoDto.consumeUrl(serviceEndpoint, consumerAddress, serviceAgreementId, url);
                String destinationPath = basePath + File.separator + fileName;

                FileUtils.copyInputStreamToFile(dataStream, new File(fileName));

                return true;

            } catch (URISyntaxException e) {
                log.error("Error consuming asset with DID " + did.getDid() +" and Service Agreement " + serviceAgreementId + " . Malformed URL. " + e.getMessage());
            } catch (IOException e) {
                log.error("Error consuming asset with DID " + did.getDid() +" and Service Agreement " + serviceAgreementId + " . Error downloading. " + e.getMessage());
            }
        }

        return false;
    }

    // TODO: to be implemented
    public Order getOrder(String orderId)   {
        return null;
    }

    // TODO: to be implemented
    public List<AssetMetadata> searchOrders()   {
        return new ArrayList<>();
    }

    private String encryptContentUrls(DID did, ArrayList<String> contentUrls, int threshold) throws IOException {
        String urls= "[" + StringsHelper.wrapWithQuotesAndJoin(contentUrls) + "]";
        log.debug("Encrypting did: "+ did.getHash());
        return getSecretStoreController().encryptDocument(did.getHash(), urls, threshold);

    }

    private String decryptContentUrls(DID did, String encryptedUrls, int threshold) throws IOException {
        log.debug("Decrypting did: "+ did.getHash());
        return getSecretStoreController().decryptDocument(did.getHash(), encryptedUrls);

    }

    private Map<String, Object> getAccessConditionParams(String did, int price)  {
        Map<String, Object> params= new HashMap<>();
        params.put("parameter.did", did);
        params.put("parameter.price", price);
        params.put("contract.paymentConditions.address", paymentConditions.getContractAddress());
        params.put("contract.accessConditions.address", accessConditions.getContractAddress());

        params.put("parameter.assetId", did.replace("did:op:", "0x"));

        return params;
    }


    public  BasicAssetInfo getBasicAssetInfo( AccessService accessService) {

        BasicAssetInfo assetInfo =  new BasicAssetInfo();

        try {

            List<Condition> conditions = accessService.conditions;
            Condition lockCondition = conditions.stream()
                    .filter(condition -> condition.name.equalsIgnoreCase("lockPayment"))
                    .findFirst()
                    .get();


            String assetIdAsString = "";

            for (Condition.ConditionParameter parameter : lockCondition.parameters) {

                if (parameter.name.equalsIgnoreCase("assetId")) {
                    assetInfo.setAssetId(EncodingHelper.hexStringToBytes((String) parameter.value));
                }

                if (parameter.name.equalsIgnoreCase("price")) {

                    assetInfo.setPrice((Integer) parameter.value);
                }
            }
        }  catch (UnsupportedEncodingException e) {

            }

        return assetInfo;

    }


}
