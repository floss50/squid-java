package com.oceanprotocol.squid.models.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.oceanprotocol.squid.core.FromJsonToModel;
import com.oceanprotocol.squid.models.AbstractModel;

//@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder(alphabetic=true)
public class Service extends AbstractModel implements FromJsonToModel {

    public enum serviceTypes {Access, Metadata, FitchainCompute, CloudCompute};

    @JsonIgnore
    protected final String ACCESS_TEMPLATE_ID= "0x044852b2a670ade5407e78fb2863c51de9fcb96542a07186fe3aeda6bb8a116d";
    @JsonIgnore
    protected final String FITCHAIN_TEMPLATE_ID= "0xc89efdaa54c0f20c7adf612882df0950f5a951637e0307cdcb4c672f298b8bc6";
    @JsonIgnore
    protected final String CLOUDCOMPUTE_TEMPLATE_ID= "0xad7c5bef027816a800da1736444fb58a807ef4c9603b7848673f7e3a68eb14a5";

    @JsonProperty
    public String type;

    @JsonProperty
    public String templateId;

    @JsonProperty
    public String serviceDefinitionId;

    @JsonProperty
    public String serviceEndpoint;

    public Service() {}

    public Service(serviceTypes type, String serviceEndpoint, String serviceDefinitionId) {
        this.type= type.toString();
        this.serviceDefinitionId= serviceDefinitionId;
        this.serviceEndpoint= serviceEndpoint;
    }

}
