package com.oceanprotocol.squid.models.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.oceanprotocol.squid.models.FromJsonToModel;
import com.oceanprotocol.squid.models.AbstractModel;

//@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder(alphabetic=true)
public class Service extends AbstractModel implements FromJsonToModel {


    @JsonIgnore
    public static final String CONSUMER_ADDRESS_PARAM = "consumerAddress";

    @JsonIgnore
    public static final String SERVICE_AGREEMENT_PARAM = "serviceAgreementId";

    @JsonIgnore
    public static final String URL_PARAM = "url";

    public enum serviceTypes {Access, Metadata, Authorization, FitchainCompute, CloudCompute};

    @JsonIgnore
    protected final String ACCESS_TEMPLATE_ID= "0x044852b2a670ade5407e78fb2863c51de9fcb96542a07186fe3aeda6bb8a116d";

    @JsonIgnore
    public static final String DEFAULT_METADATA_SERVICE_ID = "0";
    @JsonIgnore
    public static final String DEFAULT_ACCESS_SERVICE_ID = "1";
    @JsonIgnore
    public static final String DEFAULT_AUTHORIZATION_SERVICE_ID = "2";

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