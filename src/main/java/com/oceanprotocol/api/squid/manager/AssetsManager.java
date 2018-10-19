package com.oceanprotocol.api.squid.manager;

import com.oceanprotocol.api.squid.dto.KeeperDto;
import com.oceanprotocol.api.squid.dto.ProviderDto;
import org.web3j.crypto.CipherException;

import java.io.IOException;

public class AssetsManager extends BaseManager {

    public AssetsManager(KeeperDto keeperDto, ProviderDto providerDto)
            throws IOException, CipherException {
        super(keeperDto, providerDto);
    }

    public static AssetsManager getInstance(KeeperDto keeperDto, ProviderDto providerDto)
            throws IOException, CipherException {
        return new AssetsManager(keeperDto, providerDto);
    }




}
