package com.oceanprotocol.squid.dto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProviderDto {

    private static final Logger log = LogManager.getLogger(ProviderDto.class);

    private String url;

    public static ProviderDto getInstance(String url)    {
        log.debug("Getting Provider instance: " + url);
        return new ProviderDto(url);
    }

    private ProviderDto(String url) {
        this.url= url;
    }
}
