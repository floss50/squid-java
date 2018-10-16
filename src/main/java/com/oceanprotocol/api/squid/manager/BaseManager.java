package com.oceanprotocol.api.squid.manager;

import com.oceanprotocol.api.squid.dto.KeeperDto;
import com.oceanprotocol.api.squid.dto.ProviderDto;

public abstract class BaseManager {

    private KeeperDto keeperDto;
    private ProviderDto providerDto;


    public BaseManager(KeeperDto keeperDto, ProviderDto providerDto)   {
        this.keeperDto= keeperDto;
        this.providerDto= providerDto;
    }

    public KeeperDto getKeeperDto() {
        return keeperDto;
    }

    public BaseManager setKeeperDto(KeeperDto keeperDto) {
        this.keeperDto = keeperDto;
        return this;
    }

    public ProviderDto getProviderDto() {
        return providerDto;
    }

    public BaseManager setProviderDto(ProviderDto providerDto) {
        this.providerDto = providerDto;
        return this;
    }


    @Override
    public String toString() {
        return "BaseManager{" +
                "keeperDto=" + keeperDto +
                ", providerDto=" + providerDto +
                '}';
    }
}
