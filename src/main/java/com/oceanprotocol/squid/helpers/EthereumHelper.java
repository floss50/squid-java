package com.oceanprotocol.squid.helpers;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthSign;
import org.web3j.utils.Numeric;

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

    public static String getFunctionSelector(String functionDefinition)    {
        return Hash.sha3String(functionDefinition)
                .substring(0, 10);
    }

    public static byte[] getFunctionSelectorBytes(String functionDefinition)    {
        return Numeric.hexStringToByteArray(
                Hash.sha3String(functionDefinition).substring(0, 10)
        );
    }

    /**
     * It returns the size in bits of an Abi type
     * Equivalent to: web3.py size_of_type
     * @param type abi type
     * @return the number of bytes or -1 if doesn't have a fixed number of bytes
     */
    public static int sizeOfAbiType(String type) {
        if (type.contains("string"))
            return -1;
        else if (type.contains("byte"))
            return -1;
        else if (type.contains("["))
            return -1;
        else if (type.equals("bool"))
            return 8;
        else if (type.equals("address"))
            return 160;
        return Integer.parseInt(type.replaceAll("[^\\d.]", ""));
    }

}
