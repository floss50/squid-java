package com.oceanprotocol.squid.manager;

import com.oceanprotocol.keeper.contracts.ServiceAgreement;
import com.oceanprotocol.squid.core.sla.AccessSLA;
import com.oceanprotocol.squid.core.sla.SlaManager;
import com.oceanprotocol.squid.core.sla.func.LockPayment;
import com.oceanprotocol.squid.dto.AquariusDto;
import com.oceanprotocol.squid.dto.BrizoDto;
import com.oceanprotocol.squid.dto.KeeperDto;
import com.oceanprotocol.squid.exceptions.*;
import com.oceanprotocol.squid.helpers.EncodingHelper;
import com.oceanprotocol.squid.helpers.StringsHelper;
import com.oceanprotocol.squid.helpers.UrlHelper;
import com.oceanprotocol.squid.models.Account;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.DID;
import com.oceanprotocol.squid.models.Order;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import com.oceanprotocol.squid.models.asset.BasicAssetInfo;
import com.oceanprotocol.squid.models.asset.OrderResult;
import com.oceanprotocol.squid.models.brizo.InitializeAccessSLA;
import com.oceanprotocol.squid.models.service.*;
import io.reactivex.Flowable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class OceanManager extends BaseManager {

    static final Logger log= LogManager.getLogger(OceanManager.class);

    static final BigInteger DID_VALUE_TYPE= BigInteger.valueOf(2);

    protected OceanManager(KeeperDto keeperDto, AquariusDto aquariusDto)
            throws IOException, CipherException {
        super(keeperDto, aquariusDto);
    }

    /**
     * Given the KeeperDto and AquariusDto, returns a new instance of OceanManager
     * using them as attributes
     * @param keeperDto Keeper Dto
     * @param aquariusDto Provider Dto
     * @return OceanManager
     */
    public static OceanManager getInstance(KeeperDto keeperDto, AquariusDto aquariusDto)
            throws IOException, CipherException {
        return new OceanManager(keeperDto, aquariusDto);
    }

    /**
     * Given a DDO, returns a DID created using the ddo
     * @param ddo
     * @return DID
     * @throws DIDFormatException
     */
    public DID generateDID(DDO ddo) throws DIDFormatException {
        return DID.builder();
    }

    /**
     * Given a DID, scans the DIDRegistry events on-chain to resolve the
     * Metadata API url and return the DDO found
     * @param did
     * @return DDO
     * @throws IOException
     */
    public DDO resolveDID(DID did) throws EthereumException, DDOException {

        EthFilter didFilter = new EthFilter(
                DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST,
                didRegistry.getContractAddress()
        );

        try{

            final Event event= didRegistry.DIDATTRIBUTEREGISTERED_EVENT;
            final String eventSignature= EventEncoder.encode(event);
            didFilter.addSingleTopic(eventSignature);

            String didTopic= "0x" + did.getHash();
            String metadataTopic= "0x" + EncodingHelper.padRightWithZero(
                    EncodingHelper.encodeToHex(Service.serviceTypes.Metadata.toString()), 64);
            didFilter.addOptionalTopics(didTopic, metadataTopic);

            EthLog ethLog;

            try {
                ethLog = getKeeperDto().getWeb3().ethGetLogs(didFilter).send();
            }catch(IOException e){
                throw new EthereumException("Error searching DID " + did.toString() + " onchain: " + e.getMessage());
            }

            List<EthLog.LogResult> logs = ethLog.getLogs();

            int numLogs= logs.size();
            if (numLogs<1)
                throw new DDOException("No events found for " + did.toString());

            EthLog.LogResult logResult= logs.get(numLogs-1);
            List<Type> nonIndexed= FunctionReturnDecoder.decode(((EthLog.LogObject) logResult).getData(), event.getNonIndexedParameters());
            String ddoUrl= nonIndexed.get(0).getValue().toString();
            String didUrl= UrlHelper.parseDDOUrl(ddoUrl, did.toString());

            AquariusDto ddoAquariosDto= AquariusDto.getInstance(UrlHelper.getBaseUrl(didUrl));
            return ddoAquariosDto.getDDO(didUrl);

        } catch (Exception ex)  {
            log.error("Unable to retrieve DDO " + ex.getMessage());
            throw new DDOException("Unable to retrieve DDO " + ex.getMessage());
        }
    }


    /**
     * Given a DID and a Metadata API url, register on-chain the DID.
     * It allows to resolve DDO's using DID's as input
     * @param did
     * @param url metadata url
     * @return boolean success
     * @throws DIDRegisterException
     */
    public boolean registerDID(DID did, String url) throws DIDRegisterException{
        log.debug("Registering DID " + did.getHash() + " into Registry " + didRegistry.getContractAddress());

        try {

            TransactionReceipt receipt = didRegistry.registerAttribute(
                    EncodingHelper.hexStringToBytes(did.getHash()),
                    DID_VALUE_TYPE,
                    EncodingHelper.byteArrayToByteArray32(EncodingHelper.stringToBytes(Service.serviceTypes.Metadata.toString())),
                    url
            ).send();

            if (!receipt.getStatus().equals("0x1"))
                return false;
            return true;
        } catch (Exception e){
            throw new DIDRegisterException("Error registering DID " + did.getHash(), e);
        }
    }

    public DDO registerAsset(AssetMetadata metadata, String address, ServiceEndpoints serviceEndpoints, int threshold) throws DDOException {

        try {

            // Initializing DDO
            DDO ddo = new DDO(address);

            // Encrypting contentUrls and adding them to the Metadata
            ArrayList<String> urls = new ArrayList<>();
            urls.add(encryptContentUrls(ddo.getDid(), metadata.base.contentUrls, threshold));
            metadata.base.contentUrls = urls;

            // Definition of service endpoints
            String metadataEndpoint;
            if (serviceEndpoints.getMetadataEndpoint() == null)
                metadataEndpoint = getAquariusDto().getDdoEndpoint() + "/{did}";
            else
                metadataEndpoint = serviceEndpoints.getMetadataEndpoint();

            // Initialization of services supported for this asset
            MetadataService metadataService = new MetadataService(metadata, metadataEndpoint, "0");

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

            AccessService accessService = new AccessService(serviceEndpoints.getAccessEndpoint(),
                    "1",
                    serviceAgreementContract);
            accessService.purchaseEndpoint = serviceEndpoints.getPurchaseEndpoint();

            // Initializing conditions and adding to Access service
            AccessSLA sla = new AccessSLA();
            accessService.conditions = sla.initializeConditions(
                    accessService.templateId,
                    getContractAddresses(),
                    getAccessConditionParams(ddo.getDid().toString(), Integer.parseInt(metadata.base.price)));

            // Adding services to DDO
            ddo.addService(metadataService)
                    .addService(accessService);

            // Storing DDO
            DDO createdDDO = getAquariusDto().createDDO(ddo);

            // Registering DID
            registerDID(ddo.getDid(), metadataEndpoint);

            return createdDDO;
        }catch (DDOException e) {
            throw e;
        }catch (EncryptionException|InitializeConditionsException|DIDFormatException|DIDRegisterException e) {
            throw new DDOException("Error registering Asset." , e);
        }

    }


    public Flowable<OrderResult> purchaseAsset(DID did, String serviceDefinitionId, Account consumerAccount)
            throws OrderException {

        String serviceAgreementId = SlaManager.generateSlaId();
        DDO ddo;
        // Checking if DDO is already there and serviceDefinitionId is included
        try {

            ddo= resolveDID(did);
        } catch (DDOException|EthereumException e) {
            log.error("Error resolving did[" + did.getHash() + "]: " + e.getMessage());
            throw new OrderException("Error processing Order with DID " + did.getDid(), e);
        }

        try {

            return this.initializeServiceAgreement(did, ddo, serviceDefinitionId, consumerAccount, serviceAgreementId)
                    .map(event -> {
                        if (!event.state)
                            throw new ServiceAgreementException(serviceAgreementId, "There was an error with the initialization of the serviceAgreement "
                                    + EncodingHelper.toHexString(event.serviceAgreementId));
                        return EncodingHelper.toHexString(event.serviceAgreementId);
                    })
                    .switchMap(eventServiceAgreementId -> {
                        if (eventServiceAgreementId.isEmpty())
                            return Flowable.empty();
                        else {
                            log.debug("Received ExecuteServiceAgreement Event with Id: " + eventServiceAgreementId);
                            this.lockPayment(ddo, serviceDefinitionId, eventServiceAgreementId);
                            return AccessSLA.listenForGrantedAccess(accessConditions, serviceAgreementId)
                                    .map(event -> {
                                        OrderResult result = new OrderResult(serviceAgreementId, true, false);
                                        return result;
                                    })
                                    .timeout(60, TimeUnit.SECONDS
                                    )
                                    .doOnError(throwable -> {
                                        throw new ServiceAgreementException(serviceAgreementId, "Timeout waiting for AccessGranted for service agreement " + eventServiceAgreementId);
                                    });
                        }
                    });
        }catch (DDOException|ServiceException|ServiceAgreementException e){
            String msg = "Error processing Order with DID " + did.getDid() + "and ServiceAgreementID " + serviceAgreementId;
            log.error(msg  + ": " + e.getMessage());
            throw new OrderException(msg, e);
        }

    }

    private Flowable<ServiceAgreement.ExecuteAgreementEventResponse> initializeServiceAgreement(DID did, DDO ddo, String serviceDefinitionId,  Account consumerAccount, String serviceAgreementId)
            throws  DDOException, ServiceException, ServiceAgreementException {

        // We need to unlock the account before calling the purchase method
        // to be able to generate the sign of the serviceAgreement
        try {
            boolean accountUnlocked = this.getKeeperDto().unlockAccount(consumerAccount);
            if (!accountUnlocked) {
                String msg = "Account " + consumerAccount.address + " has not been unlocked";
                log.error(msg);
                throw new ServiceAgreementException(serviceAgreementId, "Account " + consumerAccount.address + " has not been unlocked");
            }

        }
        catch (Exception e){
            String msg = "Account " + consumerAccount.address + " has not been unlocked";
            log.error(msg+ ": " + e.getMessage());
            throw new ServiceAgreementException(serviceAgreementId, "Account " + consumerAccount.address + " has not been unlocked");
        }

        AccessService accessService= ddo.getAccessService(serviceDefinitionId);

        //  Consumer sign service details. It includes:
        // (templateId, conditionKeys, valuesHashList, timeoutValues, serviceAgreementId)
        String agreementSignature;
        try {
            agreementSignature = accessService.generateServiceAgreementSignature(
                    getKeeperDto().getWeb3(),
                    consumerAccount.getAddress(),
                    serviceAgreementId
            );
        }catch(IOException e){
            String msg = "Error generating signature for Service Agreement: " + serviceAgreementId;
            log.error(msg+ ": " + e.getMessage());
            throw new ServiceAgreementException(serviceAgreementId, msg, e);
        }

        InitializeAccessSLA initializePayload= new InitializeAccessSLA(
                did.toString(),
                "0x".concat(serviceAgreementId),
                serviceDefinitionId,
                agreementSignature,
                Keys.toChecksumAddress(consumerAccount.getAddress())
        );

        // 3. Send agreement details to Publisher (Brizo endpoint)
        boolean isInitialized= BrizoDto.initializeAccessServiceAgreement(accessService.purchaseEndpoint, initializePayload);

        if (!isInitialized)  {
            throw new ServiceAgreementException(serviceAgreementId, "Unable to initialize SLA using Brizo. Payload: " + initializePayload);
        }

        // 4. Listening of events
       return  AccessSLA.listenExecuteAgreement(serviceAgreement, serviceAgreementId);

    }

    private boolean lockPayment(DDO ddo, String serviceDefinitionId, String serviceAgreementId) throws ServiceException, LockPaymentException {

        AccessService accessService= ddo.getAccessService(serviceDefinitionId);
        BasicAssetInfo assetInfo = getBasicAssetInfo(accessService);

        return LockPayment.executeLockPayment(paymentConditions, serviceAgreementId, assetInfo);
    }


    public boolean consume(String serviceAgreementId, DID did, String serviceDefinitionId, String consumerAddress, String basePath) throws ConsumeServiceException {

        return consume(serviceAgreementId, did, serviceDefinitionId, consumerAddress, basePath,0);
    }


    public boolean consume(String serviceAgreementId, DID did, String serviceDefinitionId, String consumerAddress, String basePath, int threshold) throws ConsumeServiceException{

        DDO ddo;

        String checkConsumerAddress = Keys.toChecksumAddress(consumerAddress);
        String serviceEndpoint;
        String decryptedUrls;

        try {

            ddo = resolveDID(did);
            serviceEndpoint = ddo.getAccessService(serviceDefinitionId).serviceEndpoint;
            decryptedUrls = decryptContentUrls(did, ddo.metadata.base.contentUrls.get(0), threshold).replace("[", "").replace("]", "");
        }catch (EthereumException|DDOException|ServiceException|EncryptionException e) {
            String msg = "Error consuming asset with DID " + did.getDid() +" and Service Agreement " + serviceAgreementId;
            log.error(msg+ ": " + e.getMessage());
            throw new ConsumeServiceException(msg, e);
        }

        List<String> contentUrls = StringsHelper.getStringsFromJoin(decryptedUrls);

        for (String url: contentUrls) {

            // For each url we call to consume Brizo endpoint that requires consumerAddress, serviceAgreementId and url as a parameters
            try {

                String fileName = url.substring(url.lastIndexOf("/") + 1);
                String destinationPath = basePath + File.separator + fileName;

                Boolean flag = BrizoDto.consumeUrl(serviceEndpoint, checkConsumerAddress, serviceAgreementId, url, destinationPath);
                if (!flag){
                    String msg = "Error consuming asset with DID " + did.getDid() +" and Service Agreement " + serviceAgreementId;
                    log.error(msg);
                    throw new ConsumeServiceException(msg);
                }

            } catch (URISyntaxException|IOException e) {
                String msg = "Error consuming asset with DID " + did.getDid() +" and Service Agreement " + serviceAgreementId;
                log.error(msg+ ": " + e.getMessage());
                throw new ConsumeServiceException(msg, e);
            }

        }

        return true;
    }

    // TODO: to be implemented
    public Order getOrder(String orderId)   {
        return null;
    }

    // TODO: to be implemented
    public List<AssetMetadata> searchOrders()   {
        return new ArrayList<>();
    }

    private String encryptContentUrls(DID did, ArrayList<String> contentUrls, int threshold) throws EncryptionException {
        String urls= "[" + StringsHelper.wrapWithQuotesAndJoin(contentUrls) + "]";
        log.debug("Encrypting did: "+ did.getHash());
        return getSecretStoreManager().encryptDocument(did.getHash(), urls, threshold);

    }

    private String decryptContentUrls(DID did, String encryptedUrls, int threshold) throws EncryptionException {
        log.debug("Decrypting did: "+ did.getHash());
        return getSecretStoreManager().decryptDocument(did.getHash(), encryptedUrls);

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


    private  BasicAssetInfo getBasicAssetInfo( AccessService accessService) {

        BasicAssetInfo assetInfo =  new BasicAssetInfo();

        try {

            List<Condition> conditions = accessService.conditions;
            Condition lockCondition = conditions.stream()
                    .filter(condition -> condition.name.equalsIgnoreCase("lockPayment"))
                    .findFirst()
                    .get();


            for (Condition.ConditionParameter parameter : lockCondition.parameters) {

                if (parameter.name.equalsIgnoreCase("assetId")) {
                    assetInfo.setAssetId(EncodingHelper.hexStringToBytes((String) parameter.value));
                }

                if (parameter.name.equalsIgnoreCase("price")) {

                    assetInfo.setPrice((Integer) parameter.value);
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error("Exception encoding serviceAgreement " + e.getMessage());

            }

        return assetInfo;

    }


}
