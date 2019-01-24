package com.oceanprotocol.squid.api.impl;

import com.oceanprotocol.squid.api.SecretStoreAPI;
import com.oceanprotocol.squid.exceptions.EncryptionOceanException;
import com.oceanprotocol.squid.manager.SecretStoreManager;

public class SecretStoreImpl implements SecretStoreAPI{


    private SecretStoreManager secretStoreManager;

    public SecretStoreImpl(SecretStoreManager secretStoreManager){

        this.secretStoreManager = secretStoreManager;
    }


    @Override
    public String encrypt(String documentId, String content, int threshold) throws EncryptionOceanException {

            return secretStoreManager.encryptDocument(documentId, content, threshold);

    }

    @Override
    public String decrypt(String documentId, String encryptedContent) throws EncryptionOceanException {

            return secretStoreManager.decryptDocument(documentId, encryptedContent);
    }

}
