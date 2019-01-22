package com.oceanprotocol.squid.api.impl;

import com.oceanprotocol.squid.api.AccountsAPI;
import com.oceanprotocol.squid.manager.AccountsManager;
import com.oceanprotocol.squid.models.Account;
import com.oceanprotocol.squid.models.Balance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class AccountsImpl implements AccountsAPI{

    private AccountsManager accountsManager;


    public AccountsImpl(AccountsManager accountsManager) {

        this.accountsManager = accountsManager;
    }

    @Override
    public List<Account> list() {

        // TODO Handle Exception
        try {
            return accountsManager.getAccounts();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Balance balance(Account account) {

        return accountsManager.getAccountBalance(account.address);
    }

    @Override
    public TransactionReceipt requestTokens(Account account, BigInteger amount) {

        // TODO y el account???
        return accountsManager.requestTokens(amount);
    }
}
