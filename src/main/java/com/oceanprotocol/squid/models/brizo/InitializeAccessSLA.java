package com.oceanprotocol.squid.models.brizo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oceanprotocol.squid.core.FromJsonToModel;
import com.oceanprotocol.squid.models.AbstractModel;

public class InitializeAccessSLA extends AbstractModel implements FromJsonToModel {

    @JsonProperty
    public String did;

    @JsonProperty
    public String serviceAgreementId;

    @JsonProperty
    public String tyserviceDefinitionId;

    @JsonProperty
    public String signature;

    @JsonProperty
    public String consumerPublicKey;

    public InitializeAccessSLA() {}

    public InitializeAccessSLA(String did, String serviceAgreementId, String tyserviceDefinitionId, String signature, String consumerPublicKey) {
        this.did = did;
        this.serviceAgreementId = serviceAgreementId;
        this.tyserviceDefinitionId = tyserviceDefinitionId;
        this.signature = signature;
        this.consumerPublicKey = consumerPublicKey;
    }

    @Override
    public String toString() {
        return "InitializeAccessSLA{" +
                "did='" + did + '\'' +
                ", serviceAgreementId='" + serviceAgreementId + '\'' +
                ", tyserviceDefinitionId='" + tyserviceDefinitionId + '\'' +
                ", signature='" + signature + '\'' +
                ", consumerPublicKey='" + consumerPublicKey + '\'' +
                '}';
    }
}
