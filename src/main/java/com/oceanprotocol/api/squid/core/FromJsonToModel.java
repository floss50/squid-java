package com.oceanprotocol.api.squid.core;

import com.oceanprotocol.api.squid.models.AbstractModel;

import java.io.IOException;

public interface FromJsonToModel {

    static AbstractModel convertToModel(String json) throws IOException {
        throw new UnsupportedOperationException();
    };

}
