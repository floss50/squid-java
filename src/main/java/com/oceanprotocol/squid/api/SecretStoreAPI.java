package com.oceanprotocol.squid.api;


import com.oceanprotocol.squid.exceptions.EncryptionOceanException;

public interface SecretStoreAPI {

    public String encrypt(String documentId, String content, int threshold) throws EncryptionOceanException;
    public String decrypt(String documentId, String encryptedContent) throws EncryptionOceanException;


}
