package com.oceanprotocol.squid.models;


import java.math.BigInteger;

public class Balance {

    private BigInteger eth;

    private BigInteger ocn;

    public Balance() {
        this.eth= BigInteger.valueOf(0);
        this.ocn= BigInteger.valueOf(0);
    }

    public Balance(BigInteger eth, BigInteger ocn) {
        this.eth = eth;
        this.ocn = ocn;
    }

    public BigInteger getEth() {
        return eth;
    }

    public BigInteger getOcn() {
        return ocn;
    }

    @Override
    public String toString() {
        return "Balance{" +
                "eth=" + eth +
                ", ocn=" + ocn +
                '}';
    }
}