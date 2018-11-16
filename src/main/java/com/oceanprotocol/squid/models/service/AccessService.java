package com.oceanprotocol.squid.models.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder(alphabetic=true)
public class AccessService extends Service {

    @JsonProperty
    public String purchaseEndpoint;

    @JsonProperty
    public List<Condition> conditions;

    public ServiceAgreementContract serviceAgreementContract;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonPropertyOrder(alphabetic=true)
    public static class ServiceAgreementContract {

        @JsonProperty
        public String address;

        @JsonProperty
        public List<Condition.Event> events= new ArrayList<>();


        public ServiceAgreementContract() {}
    }



    public AccessService() {
        this.type= serviceTypes.Access.toString();
    }

    public AccessService(String serviceEndpoint, String serviceDefinitionId)  {
        super(serviceTypes.Access, serviceEndpoint, serviceDefinitionId);
        this.templateId= ACCESS_TEMPLATE_ID;
    }

}
