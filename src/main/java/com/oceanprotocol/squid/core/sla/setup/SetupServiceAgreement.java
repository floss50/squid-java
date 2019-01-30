package com.oceanprotocol.squid.core.sla.setup;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oceanprotocol.keeper.contracts.ServiceAgreement;
import com.oceanprotocol.squid.core.sla.ServiceAgreementHandler;
import com.oceanprotocol.squid.external.KeeperService;
import com.oceanprotocol.squid.helpers.EncodingHelper;
import com.oceanprotocol.squid.helpers.EthereumHelper;
import com.oceanprotocol.squid.models.service.template.AccessTemplate;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.crypto.CipherException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static com.oceanprotocol.squid.core.sla.ServiceAgreementHandler.*;


public class SetupServiceAgreement {

    private static final Logger log = LogManager.getLogger(SetupServiceAgreement.class);
    private Config config;

    private static final String ACCESS_SERVICE_TEMPLATE_ID= "0x044852b2a670ade5407e78fb2863c51de9fcb96542a07186fe3aeda6bb8a116d";
    private static final String ACCESS_TEMPLATE_JSON = "src/main/resources/sla/access-sla-template.json";
    private static String ACCESS_TEMPLATE_JSON_CONTENT;
    private static AccessTemplate accessTemplate;
    private static String address;
    private KeeperService keeper;
    private ServiceAgreement serviceAgreement;
    private List<String> contractAddresses= new ArrayList();


    public SetupServiceAgreement() throws Exception  {
        this(ConfigFactory.load());
    }

    public SetupServiceAgreement(Config _config) throws Exception {
        log.debug("Initializing config factory with object given");
        config= _config;
        ACCESS_TEMPLATE_JSON_CONTENT = new String(Files.readAllBytes(Paths.get(ACCESS_TEMPLATE_JSON)));
        accessTemplate= AccessTemplate.fromJSON(new TypeReference<AccessTemplate>() {}, ACCESS_TEMPLATE_JSON_CONTENT);

        address= config.getString("account.parity.address");
        keeper= getKeeper(config);
        serviceAgreement= loadServiceAgreementContract(keeper, address);

        contractAddresses.add(config.getString("contract.accessConditions.address"));
        contractAddresses.add(config.getString("contract.paymentConditions.address"));

    }

    public boolean registerTemplate() throws Exception {
        log.debug("Registering AccessService template");

        String address= serviceAgreement.getTemplateOwner(EncodingHelper.hexStringToBytes(ACCESS_SERVICE_TEMPLATE_ID)).send();

        if (!EthereumHelper.isValidAddress(address))    {
            // Registering service agreement template
            createServiceAgeementTemplate(accessTemplate);
        }
        return true;
    }

    public List<byte[]> getFingerprints() throws UnsupportedEncodingException {
        List<byte[]> fingerprints= new ArrayList();
        fingerprints.add(EncodingHelper.hexStringToBytes(EthereumHelper.getFunctionSelector(FUNCTION_LOCKPAYMENT_DEF)));
        fingerprints.add(EncodingHelper.hexStringToBytes(EthereumHelper.getFunctionSelector(FUNCTION_GRANTACCESS_DEF)));
        fingerprints.add(EncodingHelper.hexStringToBytes(EthereumHelper.getFunctionSelector(FUNCTION_RELEASEPAYMENT_DEF)));
        fingerprints.add(EncodingHelper.hexStringToBytes(EthereumHelper.getFunctionSelector(FUNCTION_REFUNDPAYMENT_DEF)));
        return fingerprints;
    }


    public List<BigInteger> getFullfillmentIndices(AccessTemplate accessTemplate, List<byte[]> fingerprints) {
        List<BigInteger> fullfillmentIndices= new ArrayList<>();
        //fullfillmentIndices.add(ServiceAgreementHandler.fetchConditionKey(ACCESS_SERVICE_TEMPLATE_ID, address, EncodingHelper.toHexString(fingerprints.get(0))));
        return fullfillmentIndices;
    }

    private boolean createServiceAgeementTemplate(AccessTemplate accessTemplate) throws UnsupportedEncodingException {

        List<byte[]> fingerprints= getFingerprints();
        /**
         * public RemoteCall<TransactionReceipt> setupAgreementTemplate(
         *   byte[] templateId,
         *   List<String> contracts,
         *   List<byte[]> fingerprints,
         *   List<BigInteger> dependenciesBits,
         *   byte[] service,
         *   List<BigInteger> fulfillmentIndices,
         *   BigInteger fulfillmentOperator) {
         *
         *     function setupTemplate(
         *         bytes32 templateId,
         *         address[] contracts,
         *         bytes4[] fingerprints,
         *         uint256[] dependenciesBits,
         *         uint8[] fulfillmentIndices,
         *         uint8 fulfillmentOperator
         *     )

        serviceAgreement.setupAgreementTemplate(
                EncodingHelper.hexStringToBytes(ACCESS_SERVICE_TEMPLATE_ID),
                contractAddresses,
                fingerprints,
                ServiceAgreementHandler.getDependenciesBits(accessTemplate.conditions),
                EncodingHelper.stringToBytes(accessTemplate.description),
                getFullfillmentIndices(accessTemplate, fingerprints),
        ).send();*/
        return true;
    }

    private static KeeperService getKeeper(Config config) throws IOException, CipherException {
        KeeperService keeper= KeeperService.getInstance(
                config.getString("keeper.url"),
                config.getString("account.parity.address"),
                config.getString("account.parity.password"),
                config.getString("account.parity.file")
        );

        keeper.setGasLimit(BigInteger.valueOf(config.getLong("keeper.gasLimit")))
                .setGasPrice(BigInteger.valueOf(config.getLong("keeper.gasPrice")));

        return keeper;
    }

    private static ServiceAgreement loadServiceAgreementContract(KeeperService keeper, String address) throws Exception, IOException, CipherException {
        return ServiceAgreement.load(
                address,
                keeper.getWeb3(),
                keeper.getCredentials(),
                keeper.getContractGasProvider());
    }

    public static void main(String[] args) throws Exception {
        SetupServiceAgreement setup= new SetupServiceAgreement();
        setup.registerTemplate();
    }



}
