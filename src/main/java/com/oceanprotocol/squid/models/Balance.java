package com.oceanprotocol.squid.models;


import java.math.BigInteger;

public class Balance {

    private BigInteger eth;

    private BigInteger ocean;

    public Balance() {
        this.eth= BigInteger.valueOf(0);
        this.ocean = BigInteger.valueOf(0);
    }

    public Balance(BigInteger eth, BigInteger ocean) {
        this.eth = eth;
        this.ocean = ocean;
    }

    public BigInteger getEth() {
        return eth;
    }

    public BigInteger getOcean() {
        return ocean;
    }

    @Override
    public String toString() {
        return "Balance{" +
                "eth=" + eth +
                ", ocean=" + ocean +
                '}';
    }
}