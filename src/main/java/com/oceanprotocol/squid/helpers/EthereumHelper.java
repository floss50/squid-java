package com.oceanprotocol.squid.helpers;

import org.web3j.crypto.Hash;

public abstract class EthereumHelper {

    public static String getFunctionSelector(String functionDefinition)    {
        return Hash.sha3String(functionDefinition)
                .substring(0, 10);
    }
}
