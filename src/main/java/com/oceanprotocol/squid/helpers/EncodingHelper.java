package com.oceanprotocol.squid.helpers;

import org.apache.commons.codec.binary.Hex;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class EncodingHelper {

    /**
     * Given a String return a Bytes32
     * @param input input string
     * @return Bytes32 output
     */
    public static Bytes32 stringToBytes32(String input) {
        byte[] byteValue = input.getBytes();
        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        return new Bytes32(byteValueLen32);
    }

    public static byte[] byteArrayToByteArray32(byte[] byteValue) {
        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        return byteValueLen32;
    }

//    public static byte[] soliditySha3(String... params) {
//        List<byte[]> arrays= Stream.of(params).map(Hash::sha3).collect(Collectors.toList());
//    }

    /**
     * Encodes a String in Hex
     * @param input string to encode
     * @return Hex string
     * @throws UnsupportedEncodingException Error encoding to Hex
     */
    public static String encodeToHex(String input) throws UnsupportedEncodingException {
        //return DatatypeConverter.printHexBinary(input.getBytes("UTF-8"));
        return Hex.encodeHexString(input.getBytes("UTF-8"));
    }

    public static byte[] hexStringToBytes(String input) throws UnsupportedEncodingException {
        return Numeric.hexStringToByteArray(input);
    }

    public static byte[] stringToBytes(String input) throws UnsupportedEncodingException {
        return hexStringToBytes(encodeToHex(input));
    }

    public static Bytes32 stringHexToBytes32(String input) throws UnsupportedEncodingException {
        return stringToBytes32(encodeToHex(input));
    }

    public static String padRightWithZero(String input, int len) {
        return String.format("%-"+ len +"s", input).replace(' ', '0');

    }
}
