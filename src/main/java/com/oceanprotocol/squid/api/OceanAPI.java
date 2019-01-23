package com.oceanprotocol.squid.api;

import com.oceanprotocol.squid.api.config.OceanConfig;
import com.oceanprotocol.squid.api.config.OceanConfigFactory;
import com.typesafe.config.Config;

import java.util.Properties;

public class OceanAPI {

    private OceanConfig oceanConfig;

    private OceanAPI(OceanConfig oceanConfig){
        this.oceanConfig = oceanConfig;
    }

    private static Properties toProperties(Config config) {
        Properties properties = new Properties();
        config.entrySet().forEach(e -> properties.setProperty(e.getKey(), config.getString(e.getKey())));
        return properties;
    }

    public static OceanAPI getInstance(Properties properties) {

        OceanConfig config = OceanConfigFactory.getOceanConfig(properties);
        OceanConfig.OceanConfigValidation validation = OceanConfig.validate(config);

        if (!validation.isValid()) {
            // TODO throw notvalidconfiguration exception
        }

        return new OceanAPI(config);
    }


    public static OceanAPI getInstance(Config config) {

       return OceanAPI.getInstance(OceanAPI.toProperties(config));
    }




}
