package com.oceanprotocol.api.squid.models;

import com.fasterxml.jackson.annotation.*;
import com.oceanprotocol.api.squid.core.FromJsonToModel;
import com.oceanprotocol.api.squid.models.asset.AssetMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static com.oceanprotocol.api.squid.models.Metadata.METADATA_TYPE;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DDO extends AbstractModel implements FromJsonToModel {

    static final Logger log= LogManager.getLogger(DDO.class);

    @JsonProperty
    public DID id;

    @JsonProperty("publicKey")
    public List<PublicKey> publicKeys= new ArrayList<>();

    @JsonProperty
    public List<Authentication> authentication= new ArrayList<>();

   // @JsonProperty("service")
    public List<Service> services= new ArrayList<>();

    @JsonProperty
    public Proof proof;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty
    public Date created;

    public Metadata metadata= null;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty
    public Date updated;


    @JsonIgnoreProperties(ignoreUnknown = true)
    static class PublicKey {

        @JsonProperty
        public String id;

        @JsonProperty
        public String type;

        @JsonProperty
        public String owner;

        @JsonProperty
        public String publicKeyPem;

        @JsonProperty
        public String publicKeyBase58;

        public PublicKey() {}
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Authentication {

        @JsonProperty
        public String type;

        @JsonProperty
        public String publicKey;

        public Authentication() {}
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Service {

        @JsonProperty
        public String id;

        @JsonProperty
        public String type;

        @JsonProperty
        public String serviceEndpoint;

        @JsonProperty
        public String description;

        @JsonProperty
        public Map<String, Object> additionalInfo= new HashMap<>();

        @JsonProperty
        public Metadata metadata;


        public Service() {}


    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Proof {

        @JsonProperty
        public String type;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        @JsonProperty
        public Date created;

        @JsonProperty
        public String creator;

        @JsonProperty
        public String signatureValue;

        public Proof() {}
    }

    @JsonSetter("service")
    public void servicesSetter(ArrayList<LinkedHashMap> services) {

        try {
            for (LinkedHashMap service: services)   {
                if (service.containsKey("metadata") && service.containsKey("type") && service.get("type").equals(METADATA_TYPE))    {
                    this.metadata= getMapperInstance().convertValue(service.get("metadata"), AssetMetadata.class);
                }

                this.services.add(getMapperInstance().convertValue(service, Service.class));
            }

        } catch (Exception ex)    {
            log.error("Unable to parse the DDO(services): " + services + ", Exception: " + ex.getMessage());
        }
    }


    @JsonGetter("service")
    public List<Service> servicesGetter()    {

        if (this.metadata != null)  {
            int counter= 0;
            for (Service service: services) {
                if (service.type!= null && service.type.equals(METADATA_TYPE) && this.metadata != null)  {
                    try {
                        service.metadata= this.metadata;
                        services.set(counter, service);
                    } catch (Exception e) {
                        log.error("Error getting metadata object");
                    }
                }
                counter++;
            }
        }

        return this.services;
    }
}
