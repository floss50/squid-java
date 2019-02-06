package com.oceanprotocol.squid.helpers;

import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSign;

import java.io.IOException;
import java.math.BigInteger;

public abstract class EthereumHelper {

    public static String hashMessage(String message)   {
        return Hash.sha3(message);
    }

    public static Sign.SignatureData signMessage(String message, ECKeyPair keyPair)   {
        return Sign.signPrefixedMessage(message.getBytes(), keyPair);
    }

    public static Sign.SignatureData signMessage(String message, Credentials credentials)   {
        return Sign.signPrefixedMessage(message.getBytes(), credentials.getEcKeyPair());
    }

    public static String recoverAddressFromSignature(Sign.SignatureData signatureData, String hashMessage) {
        String address= null;
        ECDSASignature ecdsaSignature = new ECDSASignature(
                new BigInteger(1, signatureData.getR()),
                new BigInteger(1, signatureData.getS()));

        // Iterate for each possible key to recover
        for (int i = 0; i < 4; i++) {
            BigInteger publicKey = Sign.recoverFromSignature(
                    (byte) i, ecdsaSignature, hashMessage.getBytes());
        }

        Sign.recoverFromSignature(signatureData.getV() -27, ecdsaSignature, hashMessage.getBytes());
        return address;
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
