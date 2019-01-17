package com.oceanprotocol.squid.helpers;

import org.junit.Test;
import static org.junit.Assert.*;

public class CryptoHelperTest {

    @Test
    public void soliditySha3() throws Exception {

        assertTrue(CryptoHelper.soliditySha3(1).length == 32);
    }
}