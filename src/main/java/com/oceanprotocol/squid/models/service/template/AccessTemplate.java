package com.oceanprotocol.squid.models.service.template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.oceanprotocol.squid.models.AbstractModel;
import com.oceanprotocol.squid.models.FromJsonToModel;
import com.oceanprotocol.squid.models.service.AccessService;
import com.oceanprotocol.squid.models.service.Condition;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder(alphabetic=true)
public class AccessTemplate extends AbstractModel implements FromJsonToModel {

    @JsonProperty
    public String type= "OceanProtocolServiceAgreementTemplate";

    @JsonProperty
    public String id= "0x044852b2a670ade5407e78fb2863c51de9fcb96542a07186fe3aeda6bb8a116d";

    @JsonProperty
    public String name= "dataAssetAccessServiceAgreement";

    @JsonProperty
    public String description;

    @JsonProperty
    public String creator;

    @JsonProperty
    public List<Condition> conditions= new ArrayList<>();

    @JsonProperty
    public AccessService.ServiceAgreementContract serviceAgreementContract;


    public AccessTemplate() {

    }



}