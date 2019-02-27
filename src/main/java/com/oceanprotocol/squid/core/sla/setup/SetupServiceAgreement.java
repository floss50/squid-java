package com.oceanprotocol.squid.core.sla.setup;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oceanprotocol.keeper.contracts.ServiceExecutionAgreement;
import com.oceanprotocol.squid.core.sla.ServiceAgreementHandler;
import com.oceanprotocol.squid.external.KeeperService;
import com.oceanprotocol.squid.helpers.EncodingHelper;
import com.oceanprotocol.squid.helpers.EthereumHelper;
import com.oceanprotocol.squid.models.service.Condition;
import com.oceanprotocol.squid.models.service.template.AccessTemplate;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.exceptions.ContractCallException;

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
    private static Config config;

    private static final String ACCESS_SERVICE_TEMPLATE_ID= "0x044852b2a670ade5407e78fb2863c51de9fcb96542a07186fe3aeda6bb8a116d";
    private static final String ACCESS_TEMPLATE_JSON = "src/main/resources/sla/access-sla-template.json";
    private static String ACCESS_TEMPLATE_JSON_CONTENT;
    private static AccessTemplate accessTemplate;
    private static String address;
    private KeeperService keeper;
    private ServiceExecutionAgreement sea;


    public SetupServiceAgreement() throws Exception  {
        this(ConfigFactory.load());
    }

    public SetupServiceAgreement(Config _config) throws Exception {
        log.debug("Initializing config factory with object given");
        config= _config;
        ACCESS_TEMPLATE_JSON_CONTENT = new String(Files.readAllBytes(Paths.get(ACCESS_TEMPLATE_JSON)));
        accessTemplate= AccessTemplate.fromJSON(new TypeReference<AccessTemplate>() {}, ACCESS_TEMPLATE_JSON_CONTENT);

        address= config.getString("account.parity.address");
        String seaAddress= config.getString("contract.AgreementStoreManager.address");

        keeper= getKeeper(config);
        sea= loadServiceAgreementContract(keeper, seaAddress);

    }

    public boolean registerTemplate() throws Exception {

        log.debug("Registering AccessService template");
        String address= null;

        try {
            log.debug("Getting template owner for Access Template Id " + ACCESS_SERVICE_TEMPLATE_ID);
            address= sea.getTemplateOwner(EncodingHelper.hexStringToBytes(ACCESS_SERVICE_TEMPLATE_ID)).send();
            if (!EthereumHelper.isValidAddress(address) || address.equals("0x0000000000000000000000000000000000000000") )    {
                log.debug("Template Owner Not Found: " + address);
                createServiceAgeementTemplate(accessTemplate);
            }   else    {
                log.debug("Template Owner Found: " + address);
                return false;
            }
        } catch (ContractCallException ex)  {
            log.debug("Template owner not found!");
            createServiceAgeementTemplate(accessTemplate);
        }
        return true;
    }

    public static List<String> getContractAddresses(AccessTemplate accessTemplate, String paymentConditionsAddress, String accessConditionsAddress) {
        List<String> contractAddresses= new ArrayList();
        for (Condition condition: accessTemplate.conditions)    {
            if (condition.contractName.equals("PaymentConditions"))
                contractAddresses.add(paymentConditionsAddress);
            else if (condition.contractName.equals("AccessConditions"))
                contractAddresses.add(accessConditionsAddress);
        }

        return contractAddresses;
    }

    public List<byte[]> getFingerprints() throws UnsupportedEncodingException {
        List<byte[]> fingerprints= new ArrayList();
        fingerprints.add(EncodingHelper.hexStringToBytes(EthereumHelper.getFunctionSelector(FUNCTION_LOCKPAYMENT_DEF)));
        fingerprints.add(EncodingHelper.hexStringToBytes(EthereumHelper.getFunctionSelector(FUNCTION_GRANTACCESS_DEF)));
        fingerprints.add(EncodingHelper.hexStringToBytes(EthereumHelper.getFunctionSelector(FUNCTION_RELEASEPAYMENT_DEF)));
        fingerprints.add(EncodingHelper.hexStringToBytes(EthereumHelper.getFunctionSelector(FUNCTION_REFUNDPAYMENT_DEF)));
        return fingerprints;
    }

    private boolean createServiceAgeementTemplate(AccessTemplate accessTemplate) throws UnsupportedEncodingException {

        try {
            TransactionReceipt receipt = sea.setupTemplate(
                    EncodingHelper.hexStringToBytes(ACCESS_SERVICE_TEMPLATE_ID),
                    getContractAddresses(
                            accessTemplate,
                            config.getString("contract.LockRewardCondition.address"),
                            config.getString("contract.AccessSecretStoreCondition.address")),
                    getFingerprints(),
                    ServiceAgreementHandler.getDependenciesBits(),
                    ServiceAgreementHandler.getFullfillmentIndices(accessTemplate.conditions),
                    BigInteger.ONE

            ).send();

            log.debug("Setup template function called. Status: " + receipt.getStatus());

        } catch (Exception ex)  {
            log.error("Unable to register Service Agreement Template: " + ex.getMessage());
            return false;
        }
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

    private static ServiceExecutionAgreement loadServiceAgreementContract(KeeperService keeper, String address) throws Exception, IOException, CipherException {
        return ServiceExecutionAgreement.load(
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
