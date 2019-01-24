package com.oceanprotocol.squid.api.impl;

import com.oceanprotocol.squid.api.AccountsAPI;
import com.oceanprotocol.squid.exceptions.EthereumOceanException;
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
    public List<Account> list() throws EthereumOceanException {

            return accountsManager.getAccounts();
    }

    @Override
    public Balance balance(Account account) throws EthereumOceanException {

        return accountsManager.getAccountBalance(account.address);
    }

    @Override
    public TransactionReceipt requestTokens(BigInteger amount) throws EthereumOceanException {

        return accountsManager.requestTokens(amount);
    }
}
