package com.oceanprotocol.api.squid.models;


import java.math.BigInteger;

public class Balance {

    public BigInteger eth;

    public BigInteger ocn;

    public Balance() {
        this.eth= BigInteger.valueOf(-1);
        this.ocn= BigInteger.valueOf(-1);
    }

    public Balance(BigInteger eth, BigInteger ocn) {
        this.eth = eth;
        this.ocn = ocn;
    }

    @Override
    public String toString() {
        return "Balance{" +
                "eth=" + eth +
                ", ocn=" + ocn +
                '}';
    }
}
