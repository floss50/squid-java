package com.oceanprotocol.squid.helpers;

import org.apache.commons.codec.binary.Hex;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.utils.Numeric;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

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

    public static byte[] integerToBytes(int input) {
        return ByteBuffer.allocate(Integer.BYTES).putInt(input).array();
    }

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

    public static String toHexString(byte[] input)  {
        return Numeric.toHexString(input);
    }

    /**
     *     if is_array_type(abi_type):
     *         sub_type = sub_type_of_array_type(abi_type)
     *         return "".join([remove_0x_prefix(hex_encode_abi_type(sub_type, v, 256)) for v in value])
     *     elif is_bool_type(abi_type):
     *         return to_hex_with_size(value, data_size)
     *     elif is_uint_type(abi_type):
     *         return to_hex_with_size(value, data_size)
     *     elif is_int_type(abi_type):
     *         return to_hex_twos_compliment(value, data_size)
     *     elif is_address_type(abi_type):
     *         return pad_hex(value, data_size)
     *     elif is_bytes_type(abi_type):
     *         if is_bytes(value):
     *             return encode_hex(value)
     *         else:
     *             return value
     *     elif is_string_type(abi_type):
     *         return to_hex(text=value)
     * @param type
     * @param value
     * @return
     */


    public static String hexEncodeAbiType(String type, Object value) throws UnsupportedEncodingException {

        //int size = EthereumHelper.sizeOfAbiType(type);

        if (type.contains("[")) {
            String subType= type.replaceAll("\\[\\]", "");
            Object[] items= (String[]) value;
            String result= "";
            for (Object item: items)    {
                result= result + hexEncodeAbiType(subType, item);
            }
            return result;
        } else if (type.contains("bool"))
            return TypeEncoder.encode(new Bool((boolean) value));
        else if (type.contains("uint"))
            return TypeEncoder.encode(new Uint(BigInteger.valueOf((int) value)));
        else if (type.contains("address"))
            return TypeEncoder.encode(new Address((String) value));
        else if (type.contains("bytes"))
            return encodeToHex((String) value);
        else if (type.equals("string"))
            return encodeToHex((String) value);

        return encodeToHex((String) value);

    }

}
