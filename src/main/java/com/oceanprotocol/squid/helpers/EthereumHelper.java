package com.oceanprotocol.squid.helpers;

import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

public abstract class EthereumHelper {

    public static String getFunctionSelector(String functionDefinition)    {
        return Hash.sha3String(functionDefinition)
                .substring(0, 10);
    }

    public static byte[] getFunctionSelectorBytes(String functionDefinition)    {
        return Numeric.hexStringToByteArray(
                Hash.sha3String(functionDefinition).substring(0, 10)
        );
    }
}
