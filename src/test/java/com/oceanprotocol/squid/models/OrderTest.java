package com.oceanprotocol.squid.models;

import com.oceanprotocol.squid.models.asset.AssetMetadata;
import org.junit.Test;

import static org.junit.Assert.*;

public class OrderTest {

    @Test
    public void toStringTest() {
        Order order= new Order("id", new AssetMetadata(), 10);
        assertTrue(order.toString().contains("id='id'"));
    }
}