package com.oceanprotocol.squid.helpers;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSign;

import java.io.IOException;

public abstract class EthereumHelper {


    public static Sign.SignatureData signMessage(String message, Credentials credentials)   {
        return Sign.signPrefixedMessage(message.getBytes(), credentials.getEcKeyPair());
    }

    public static String ethSignMessage(Web3j web3, String message, String address) throws IOException {
        EthSign ethSign= web3.ethSign(address, message).send();
        return ethSign.getResult();
    }

    public static String ethEncodeAndSignMessage(Web3j web3, String message, String address) throws IOException {
        String hash= Hash.sha3(EncodingHelper.encodeToHex(message));
        return ethSignMessage(web3, hash, address);
    }

    public static String remove0x(String input) {
        return input.replaceAll("0x", "");
    }

    public static String add0x(String input) {
        if (!input.startsWith("0x"))
            return "0x" + input;
        return input;
    }


    public static String getFunctionSelector(String functionDefinition)    {
        return Hash.sha3String(functionDefinition)
                .substring(0, 10);
    }


}
