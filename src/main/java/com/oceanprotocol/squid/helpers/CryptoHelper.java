package com.oceanprotocol.squid.helpers;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.Utils;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.web3j.crypto.Hash;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.google.common.base.Preconditions;
import org.web3j.utils.Numeric;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;

public abstract class CryptoHelper {

    /**
     * The Constant DIGITS.
     */
    private static final char[] DIGITS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final BigInteger MASK_256 = BigInteger.ONE.shiftLeft(256).subtract(BigInteger.ONE);


    public static byte[] sign(byte[] hash, EdDSAPrivateKey privateKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature edDsaSigner= new EdDSAEngine(MessageDigest.getInstance("SHA-512"));
        edDsaSigner.initSign(privateKey);
        edDsaSigner.update(hash);
        return edDsaSigner.sign();

    }

    public static byte[] getSha3HashRaw(byte[] input) {
        SHA3.DigestSHA3 md = new SHA3.DigestSHA3(256); //same as DigestSHA3 md = new SHA3.Digest256();
        md.update(input);
        return md.digest();
    }

    public static String getSha3HashHex(byte[] input) {
        SHA3.DigestSHA3 md = new SHA3.DigestSHA3(256); //same as DigestSHA3 md = new SHA3.Digest256();
        md.update(input);
        String id = getHex(md.digest());
        return id;
    }

    public static byte[] soliditySha3(Object... data) {
        if (data.length == 1) {
            return Hash.sha3(toBytes(data[0]));
        }
        List<byte[]> arrays = Stream.of(data).map(CryptoHelper::toBytes).collect(Collectors.toList());
        ByteBuffer buffer = ByteBuffer.allocate(arrays.stream().mapToInt(a -> a.length).sum());
        for (byte[] a : arrays) {
            buffer.put(a);
        }
        byte[] array = buffer.array();
        assert buffer.position() == array.length;
        return Hash.sha3(array);
    }

    /**
     * Gets the hex.
     *
     * @param data the data
     * @return the hex
     */
    public static String getHex(byte[] data) {
        final int l = data.length;
        final char[] outData = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            outData[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            outData[j++] = DIGITS[0x0F & data[i]];
        }

        return new String(outData);
    }

    /**
     * Private key from hex.
     *
     * @param hex the hex
     * @return the private key
     * @throws InvalidKeySpecException the invalid key spec exception
     */
    public static PrivateKey getPrivateKeyFromHex(String hex) throws InvalidKeySpecException {

        final PKCS8EncodedKeySpec encoded = new PKCS8EncodedKeySpec(Utils.hexToBytes(hex));
        final PrivateKey privKey = new EdDSAPrivateKey(encoded);

        return privKey;

    }

    public static PrivateKey getPrivateKeyFromString(String key) throws InvalidKeySpecException, UnsupportedEncodingException {
        return getPrivateKeyFromHex(stringToHex(key));
    }


    /**
     * Public key from hex.
     *
     * @param hex the hex
     * @return the public key
     * @throws InvalidKeySpecException the invalid key spec exception
     */
    public static PublicKey getPublicKeyFromHex(String hex) throws InvalidKeySpecException {
        final X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Utils.hexToBytes(hex));
        final PublicKey pubKey = new EdDSAPublicKey(pubKeySpec);

        return pubKey;
    }

    public static PublicKey getPublicKeyFromString(String key) throws InvalidKeySpecException, UnsupportedEncodingException {
        if (key.startsWith("0x"))
            key= key.replaceAll("0x", "");
        return getPublicKeyFromHex(key);
    }

    public static String stringToHex(String arg) throws UnsupportedEncodingException {
        return String.format("%x", new BigInteger(1, arg.getBytes("UTF-8")));
    }

    public static byte[] toBytes(Object obj) {
        if (obj instanceof byte[]) {
            int length = ((byte[]) obj).length;
            Preconditions.checkArgument(length <= 32);
            if (length < 32) {
                return Arrays.copyOf((byte[]) obj, 32);
            }
            return (byte[]) obj;
        } else if (obj instanceof BigInteger) {
            BigInteger value = (BigInteger) obj;
            if (value.signum() < 0) {
                value = MASK_256.and(value);
            }
            return Numeric.toBytesPadded(value, 32);
        } else if (obj instanceof Address) {
            Uint uint = ((Address) obj).toUint160();
            return Numeric.toBytesPadded(uint.getValue(), 20);
        } else if (obj instanceof Uint256) {
            Uint uint = (Uint) obj;
            return Numeric.toBytesPadded(uint.getValue(), 32);
        } else if (obj instanceof Uint64) {
            Uint uint = (Uint) obj;
            return Numeric.toBytesPadded(uint.getValue(), 8);
        } else if (obj instanceof Number) {
            long l = ((Number) obj).longValue();
            return toBytes(BigInteger.valueOf(l));
        }
        throw new IllegalArgumentException(obj.getClass().getName());
    }

}
