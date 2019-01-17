package com.oceanprotocol.squid.models.service;

public class Endpoints {

    private String accessEndpoint;
    private String purchaseEndpoint;
    private String metadataEndpoint;

    public Endpoints() {
    }

    public Endpoints(String accessEndpoint, String purchaseEndpoint, String metadataEndpoint) {
        this.accessEndpoint = accessEndpoint;
        this.purchaseEndpoint = purchaseEndpoint;
        this.metadataEndpoint = metadataEndpoint;
    }

    public String getAccessEndpoint() {
        return accessEndpoint;
    }

    public Endpoints setAccessEndpoint(String accessEndpoint) {
        this.accessEndpoint = accessEndpoint;
        return this;
    }

    public String getPurchaseEndpoint() {
        return purchaseEndpoint;
    }

    public Endpoints setPurchaseEndpoint(String purchaseEndpoint) {
        this.purchaseEndpoint = purchaseEndpoint;
        return this;
    }

    public String getMetadataEndpoint() {
        return metadataEndpoint;
    }

    public Endpoints setMetadataEndpoint(String metadataEndpoint) {
        this.metadataEndpoint = metadataEndpoint;
        return this;
    }
}