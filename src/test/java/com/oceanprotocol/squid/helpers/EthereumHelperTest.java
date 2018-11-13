package com.oceanprotocol.squid.helpers;

import org.junit.Test;

import static org.junit.Assert.*;

public class EthereumHelperTest {

    @Test
    public void getFunctionSelector() {

        assertEquals("0xc48d6d5e",
                EthereumHelper.getFunctionSelector("sendMessage(string,address)"));

        assertEquals("0x668453f0",
                EthereumHelper.getFunctionSelector("lockPayment(bytes32,bytes32,uint256)"));
    }
}