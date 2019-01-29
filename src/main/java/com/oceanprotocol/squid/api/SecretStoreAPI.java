package com.oceanprotocol.squid.api;


import com.oceanprotocol.squid.exceptions.EncryptionException;

/**
 * Exposes the Public API related with encryption functionalities
 */
public interface SecretStoreAPI {

    /**
     * Encrypts a document using Secret Store
     * @param documentId
     * @param content
     * @param threshold
     * @return a String with the encrypted content
     * @throws EncryptionException
     */
    public String encrypt(String documentId, String content, int threshold) throws EncryptionException;

    /**
     * Decrypts a document using Secret Store
     * @param documentId
     * @param encryptedContent
     * @return a String with the decrypted content
     * @throws EncryptionException
     */
    public String decrypt(String documentId, String encryptedContent) throws EncryptionException;


}
