package com.oceanprotocol.api.squid.manager;


import com.oceanprotocol.secretstore.auth.ConsumerWorker;
import com.oceanprotocol.secretstore.auth.PublisherWorker;
import com.oceanprotocol.secretstore.core.EvmDto;
import com.oceanprotocol.secretstore.core.SecretStoreDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.crypto.CipherException;

import java.io.IOException;

public class SecretStoreController {

    static final Logger log= LogManager.getLogger(SecretStoreController.class);

    private SecretStoreDto secretStoreDto;
    private EvmDto evmDto;
    private PublisherWorker publisherWorker;
    private ConsumerWorker consumerWorker;

    private SecretStoreController(SecretStoreDto ssDto, EvmDto evmDto) {
        this.secretStoreDto= ssDto;
        this.evmDto= evmDto;
        this.publisherWorker= new PublisherWorker(ssDto, evmDto);
        this.consumerWorker= new ConsumerWorker(ssDto, evmDto);
    }


    public static SecretStoreController getInstance(SecretStoreDto ssDto, EvmDto evmDto)
            throws IOException, CipherException {
        return new SecretStoreController(ssDto, evmDto);
    }

    public String encryptDocument(String resourceId, String content, int threshold) throws IOException    {
        return publisherWorker.encryptDocument(resourceId, content, threshold);
    }

    public String decryptDocument(String resourceId, String encryptedContent) throws IOException {
        return consumerWorker.decryptDocument(resourceId, encryptedContent);
    }
}
