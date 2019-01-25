package com.oceanprotocol.squid.api;


import com.oceanprotocol.squid.exceptions.EncryptionException;

public interface SecretStoreAPI {

    public String encrypt(String documentId, String content, int threshold) throws EncryptionException;
    public String decrypt(String documentId, String encryptedContent) throws EncryptionException;


}
