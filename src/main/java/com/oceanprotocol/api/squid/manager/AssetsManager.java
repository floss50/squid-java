package com.oceanprotocol.api.squid.manager;

import com.oceanprotocol.api.squid.dto.KeeperDto;
import com.oceanprotocol.api.squid.dto.ProviderDto;

public class AssetsManager extends BaseManager {

    public AssetsManager(KeeperDto keeperDto, ProviderDto providerDto) {
        super(keeperDto, providerDto);
    }

    public static AssetsManager getInstance(KeeperDto keeperDto, ProviderDto providerDto)  {
        return new AssetsManager(keeperDto, providerDto);
    }




}
