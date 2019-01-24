package com.oceanprotocol.squid.api;


public interface SecretStoreAPI {

    public String encrypt(String documentId, String content, int threshold);
    public String decrypt(String documentId, String encryptedContent);


}
