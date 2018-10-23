package com.oceanprotocol.squid.manager;

import com.oceanprotocol.secretstore.auth.ConsumerWorker;
import com.oceanprotocol.secretstore.auth.PublisherWorker;
import com.oceanprotocol.secretstore.core.EvmDto;
import com.oceanprotocol.secretstore.core.SecretStoreDto;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.*;

public class SecretStoreControllerIT {

    private static PublisherWorker publisherWorker;
    private static ConsumerWorker consumerWorker;

    private static final Config config = ConfigFactory.load();

    private static final String CONTENT= "https://i.giphy.com/media/3o6Zt481isNVuQI1l6/giphy.webp";

    @BeforeClass
    public static void setUp() throws Exception {
        SecretStoreDto ssDto= SecretStoreDto.builder(config.getString("secretstore.url"));
        EvmDto evmDto= EvmDto.builder(
                config.getString("keeper.url"),
                config.getString("account.address"),
                config.getString("account.password")
        );

        publisherWorker= new PublisherWorker(ssDto, evmDto);
        consumerWorker= new ConsumerWorker(ssDto, evmDto);
    }

    @Test
    public void encryptDocument() throws IOException {
        String serviceAgreementId= UUID.randomUUID().toString();
        String encryptedContent= publisherWorker.encryptDocument(serviceAgreementId, CONTENT);

        String decryptedContent= consumerWorker.decryptDocument(serviceAgreementId, encryptedContent);

        assertEquals(CONTENT, decryptedContent);

    }
}