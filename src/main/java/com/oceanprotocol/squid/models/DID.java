package com.oceanprotocol.squid.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DID {

    @JsonProperty
    public String did;

    public static final String PREFIX= "did:op:";

    public DID()    {
        this.setEmptyDID();
    }

    public DID(String did) throws DIDFormatException {
        setDid(did);
    }

    public static DID getFromHash(String hash) throws DIDFormatException {
        return new DID(PREFIX + hash);
    }

    public String getDid() {
        return did;
    }

    public String getHash() {
        return did.substring(PREFIX.length());
    }

    @Override
    public String toString() {
        return did;
    }

    public DID setEmptyDID() {
        this.did= "";
        return this;
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

    public static class DIDGenerationException extends Exception {
        public DIDGenerationException(String s) {
            super(s);
        }
    }
}
