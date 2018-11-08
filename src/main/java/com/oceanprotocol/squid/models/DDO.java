package com.oceanprotocol.squid.models;

import com.fasterxml.jackson.annotation.*;
import com.google.api.client.util.Base64;
import com.oceanprotocol.squid.core.FromJsonToModel;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static com.oceanprotocol.squid.models.DDO.PublicKey.ETHEREUM_KEY_TYPE;

@JsonPropertyOrder(alphabetic=true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DDO extends AbstractModel implements FromJsonToModel {

    static final Logger log= LogManager.getLogger(DDO.class);

    private static final String DDO_PROOF_TYPE= "DDODataSHA3Signature";
    private static final String UUID_PROOF_TYPE= "UUIDSignature";

    private static final String MODEL_CHARSET= "UTF-8";

    @JsonProperty("@context")
    public String context= "https://w3id.org/future-method/v1";

    @JsonProperty
    public String id;

    @JsonIgnore
    private DID did;

    @JsonProperty("publicKey")
    public List<PublicKey> publicKeys= new ArrayList<>();

    @JsonProperty
    public List<Authentication> authentication= new ArrayList<>();

    //@JsonProperty("service")
    @JsonIgnore
    public List<Service> services= new ArrayList<>();

    @JsonProperty
    public Proof proof;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    @JsonProperty
    public Date created;

    @JsonIgnore
    public AssetMetadata metadata= null;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    @JsonProperty
    public Date updated;


    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonPropertyOrder(alphabetic=true)
    static class PublicKey {

        public static final String ETHEREUM_KEY_TYPE= "EthereumECDSAKey";

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

        public PublicKey(String id, String type, String owner)  {
            this.id= id;
            this.type= type;
            this.owner= owner;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonPropertyOrder(alphabetic=true)
    static class Authentication {

        @JsonProperty
        public String type;

        @JsonProperty
        public String publicKey;

        public Authentication() {}
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonPropertyOrder(alphabetic=true)
    public static class Service {

        public enum serviceTypes {Metadata, Compute, Consumption, Other};

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

        //@JsonProperty
        public AssetMetadata metadata;


        public Service() {}


    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonPropertyOrder(alphabetic=true)
    static class Proof {

        @JsonProperty
        public String type;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
        @JsonProperty
        public Date created;

        @JsonProperty
        public String creator;

        @JsonProperty
        public String signatureValue;

        public Proof() {}

        public Proof(String type, String creator, String signature) {
            this.type= type;
            this.creator= creator;
            this.signatureValue= signature;
            this.created= new Date();
        }

        public Proof(String type, String creator, byte[] signature) {
            this(type, creator, Base64.encodeBase64URLSafeString(signature));
        }
    }

    public DDO() {
    }

    public DDO(AssetMetadata metadata, String publicKey, String serviceUrl) throws DID.DIDGenerationException, DID.DIDFormatException {
        this.did= generateDID(publicKey);
        this.id= did.toString();
        this.created= new Date();

        String did= this.id.toString();

        this.publicKeys.add( new DDO.PublicKey(did, ETHEREUM_KEY_TYPE, did));
        DDO.Service service= new DDO.Service();
        service.type= Service.serviceTypes.Metadata.toString();
        service.serviceEndpoint= serviceUrl;
        service.metadata= metadata;
        this.metadata= metadata;

        this.services.add(service);
    }

    @JsonSetter("service")
    public void servicesSetter(ArrayList<LinkedHashMap> services) {

        try {
            for (LinkedHashMap service: services)   {
                if (service.containsKey("metadata") && service.containsKey("type") && service.get("type").equals(Metadata.METADATA_TYPE))    {
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
                if (service.type!= null && service.type.equals(Metadata.METADATA_TYPE) && this.metadata != null)  {
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

    public DID generateDID(String address)    throws DID.DIDFormatException {
        this.did= DID.builder();
        this.id= did.toString();

        log.debug("Id generated: " + this.id);
        this.proof= new Proof(UUID_PROOF_TYPE, address, this.id);

        return this.did;
    }

    public DID getDid() {
        return did;
    }

    /**
     *future integration, test pending
     */
    /**
    public DID generateDID(EdDSAPublicKey publicKey, EdDSAPrivateKey privateKey) throws DID.DIDGenerationException {
        try {
            String json = toJson();
            byte[] sha3hash = CryptoHelper.getSha3HashRaw(json.getBytes(Charset.forName(MODEL_CHARSET)));
            byte[] signature = CryptoHelper.sign(sha3hash, privateKey);

            Ed25519Sha256Fulfillment fulfillment = new Ed25519Sha256Fulfillment(publicKey, signature);

            this.proof= new Proof(DDO_PROOF_TYPE, publicKey.getA().toString(), fulfillment.getEncoded());
            String idHash= CryptoHelper.getSha3HashHex(this.toJson().getBytes(MODEL_CHARSET));
            log.debug("Id generated: " + idHash);

            this.id= DID.getFromHash(idHash);
            return this.id;

        } catch (Exception ex)  {
            log.error("Error generating DID " + ex.getMessage());
            throw new DID.DIDGenerationException("Error generating DID " + ex.getMessage());
        }
    }
     */
}
