package com.oceanprotocol.squid.api;

import com.oceanprotocol.squid.models.Account;

public interface SecretStoreAPI {

    public String encrypt(String documentId, String content, Account account, int threshold);
    public String decrypt(String documentId, String encryptedContent, Account account);


}
