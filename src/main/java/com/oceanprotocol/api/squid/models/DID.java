package com.oceanprotocol.api.squid.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DID {

    @JsonProperty
    public String did;

    public static final String PREFIX= "did:op:";

    public DID(String did) throws DIDFormatException {
        setDid(did);
    }

    public String getDid() {
        return did;
    }

    @Override
    public String toString() {
        return did;
    }

    public DID setDid(String did) throws DIDFormatException {
        if (!did.startsWith(PREFIX))
            throw new DIDFormatException("Invalid DID Format, it should starts by " + PREFIX);
        this.did = did;
        return this;
    }


    public class DIDFormatException extends Exception {
        public DIDFormatException(String s) {
            super(s);
        }
    }
}
