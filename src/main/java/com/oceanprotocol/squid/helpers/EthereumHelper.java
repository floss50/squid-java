package com.oceanprotocol.squid.helpers;

import org.web3j.crypto.Hash;

public abstract class EthereumHelper {

    public static String getFunctionSelector(String functionDefinition)    {
        String fullSignature= Hash.sha3String(functionDefinition);
        return fullSignature.substring(0, 10);
    }
}
