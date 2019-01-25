package com.oceanprotocol.squid.manager;


import com.oceanprotocol.secretstore.auth.ConsumerWorker;
import com.oceanprotocol.secretstore.auth.PublisherWorker;
import com.oceanprotocol.secretstore.core.EvmDto;
import com.oceanprotocol.secretstore.core.SecretStoreDto;
import com.oceanprotocol.squid.exceptions.EncryptionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class SecretStoreManager {

    static final Logger log= LogManager.getLogger(SecretStoreManager.class);

    private SecretStoreDto secretStoreDto;
    private EvmDto evmDto;
    private PublisherWorker publisherWorker;
    private ConsumerWorker consumerWorker;

    private SecretStoreManager(SecretStoreDto ssDto, EvmDto evmDto) {
        this.secretStoreDto= ssDto;
        this.evmDto= evmDto;
        this.publisherWorker= new PublisherWorker(ssDto, evmDto);
        this.consumerWorker= new ConsumerWorker(ssDto, evmDto);
    }


    public static SecretStoreManager getInstance(SecretStoreDto ssDto, EvmDto evmDto) {
        return new SecretStoreManager(ssDto, evmDto);
    }

    public String encryptDocument(String resourceId, String content, int threshold) throws EncryptionException {

        try {

            return publisherWorker.encryptDocument(resourceId, content, threshold);

        }catch (IOException e){
            throw new EncryptionException("Error encrypting Document", e);
        }
    }

    public String decryptDocument(String resourceId, String encryptedContent) throws EncryptionException {

        try {

            return consumerWorker.decryptDocument(resourceId, encryptedContent);

        }catch (IOException e){
            throw new EncryptionException("Error decrypting Document", e);
        }
    }
}
