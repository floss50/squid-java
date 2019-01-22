package com.oceanprotocol.squid.api.impl;

import com.oceanprotocol.squid.api.SecretStoreAPI;
import com.oceanprotocol.squid.manager.SecretStoreManager;
import com.oceanprotocol.squid.models.Account;

import java.io.IOException;

public class SecretStoreImpl implements SecretStoreAPI{


    private SecretStoreManager secretStoreManager;

    public SecretStoreImpl(SecretStoreManager secretStoreManager){

        this.secretStoreManager = secretStoreManager;
    }


    @Override
    public String encrypt(String documentId, String content, Account account, int threshold) {

        // TODO HAndle Exception
        // TODO and the account???
        try {
            return secretStoreManager.encryptDocument(documentId, content, threshold);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String decrypt(String documentId, String encryptedContent, Account account) {

        // TODO Handle Exception
        // TODO and the account???
        try {
            return secretStoreManager.decryptDocument(documentId, encryptedContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
