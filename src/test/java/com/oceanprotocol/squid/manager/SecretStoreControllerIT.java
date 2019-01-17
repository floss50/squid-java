package com.oceanprotocol.squid.manager;

import com.oceanprotocol.secretstore.auth.ConsumerWorker;
import com.oceanprotocol.secretstore.auth.PublisherWorker;
import com.oceanprotocol.secretstore.core.EvmDto;
import com.oceanprotocol.secretstore.core.SecretStoreDto;
import com.oceanprotocol.squid.helpers.StringsHelper;
import com.oceanprotocol.squid.models.DID;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SecretStoreControllerIT {


    static final Logger log= LogManager.getLogger(SecretStoreControllerIT.class);

    private static PublisherWorker publisherWorker;
    private static ConsumerWorker consumerWorker;

    private static final Config config = ConfigFactory.load();

    private static final String URL1= "https://i.giphy.com/media/3o6Zt481isNVuQI1l6/giphy.webp";
    private static final String URL2= "https://disney.com";

    @BeforeClass
    public static void setUp() throws Exception {
        SecretStoreDto ssDto= SecretStoreDto.builder(config.getString("secretstore.url"));
        EvmDto publisherEvmDto= EvmDto.builder(
                config.getString("keeper.url"),
                config.getString("account.parity.address"),
                config.getString("account.parity.password")
        );

        EvmDto consumerEvmDto= EvmDto.builder(
                config.getString("keeper.url"),
                config.getString("account.parity.address3"),
                config.getString("account.parity.password3")
        );

        publisherWorker= new PublisherWorker(ssDto, publisherEvmDto);
        consumerWorker= new ConsumerWorker(ssDto, consumerEvmDto);
    }

    @Test
    public void encryptDocument() throws Exception {
        String serviceAgreementId= DID.builder().getHash();

        List<String> contentUrls= new ArrayList<>();
        contentUrls.add(URL1);
        contentUrls.add(URL2);

        String urls= "[" + StringsHelper.wrapWithQuotesAndJoin(contentUrls) + "]";

        log.debug("Encrypting did: " + serviceAgreementId + " and urls: " + urls);
        String encryptedContent= publisherWorker.encryptDocument(serviceAgreementId, urls, 0);

        String decryptedContent= consumerWorker.decryptDocument(serviceAgreementId, encryptedContent);

        assertEquals(urls, decryptedContent);

    }
}