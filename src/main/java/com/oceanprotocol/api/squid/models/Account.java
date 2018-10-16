package com.oceanprotocol.api.squid.models;

public class Account {


    public String name;

    public Balance balance;

    private Account() {}

    public Account(String name) {
        this(name, new Balance());
    }
    public Account(String name, Balance balance) {
        this.name = name;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
