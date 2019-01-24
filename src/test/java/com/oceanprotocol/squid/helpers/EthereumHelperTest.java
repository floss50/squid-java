package com.oceanprotocol.squid.helpers;

import org.junit.Test;

import static org.junit.Assert.*;

public class EthereumHelperTest {


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