package com.oceanprotocol.squid.helpers;

import org.junit.Ignore;
import org.junit.Test;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;

import static org.junit.Assert.*;

public class EthereumHelperTest {

    public static final String PRIVATE_KEY_STRING = "a392604efc2fad9c0b3da43b5f698a2e3f270f170d859912be0d54742275c5f6";
    public static final String PUBLIC_KEY_STRING = "0x506bc1dc099358e5137292f4efdd57e400f29ba5132aa5d12b18dac1c1f6aab"
            + "a645c0b7b58158babbfa6c6cd5a48aa7340a8749176b120e8516216787a13dc76";
    public static final String ADDRESS = "0xef678007d18427e6022059dbc264f27507cd1ffc";

    public static final BigInteger PRIVATE_KEY = Numeric.toBigInt(PRIVATE_KEY_STRING);
    public static final BigInteger PUBLIC_KEY = Numeric.toBigInt(PUBLIC_KEY_STRING);

    static final ECKeyPair KEY_PAIR = new ECKeyPair(PRIVATE_KEY, PUBLIC_KEY);

    @Test
    public void signMessage() throws IOException, CipherException {
        String message = "Hi there";
        Sign.SignatureData signatureData = EthereumHelper.signMessage(message, KEY_PAIR);

        assertTrue(signatureData.getR().length == 32);
        assertTrue(signatureData.getS().length == 32);

    }

    @Ignore
    @Test
    public void signAndValidateMessage() throws IOException, CipherException {
        String message = "Hi there dude";
        String hashMessage= EthereumHelper.hashMessage(message);
        Sign.SignatureData signatureData = EthereumHelper.signMessage(message, KEY_PAIR);

        String address= EthereumHelper.recoverAddressFromSignature(signatureData, hashMessage);

        assertEquals(ADDRESS, address);

    }

    @Test
    public void remove0x() {
        assertEquals("1234", EthereumHelper.remove0x("0x1234"));
    }

    @Test
    public void add0x() {
        assertEquals("0x1234", EthereumHelper.add0x("1234"));
        assertEquals("0x1234", EthereumHelper.add0x("0x1234"));
    }

    @Test
    public void getFunctionSelector() {
        String functionDefinition= "lockPayment(bytes32,bytes32,uint256)";
        assertEquals("0x668453f0", EthereumHelper.getFunctionSelector(functionDefinition));
    }
}