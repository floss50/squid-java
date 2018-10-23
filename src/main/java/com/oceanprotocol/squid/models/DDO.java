package com.oceanprotocol.squid.models;

import com.bigchaindb.cryptoconditions.types.Ed25519Sha256Fulfillment;
import com.fasterxml.jackson.annotation.*;
import com.google.api.client.util.Base64;
import com.oceanprotocol.squid.core.FromJsonToModel;
import com.oceanprotocol.squid.helpers.CryptoHelper;
import com.oceanprotocol.squid.models.asset.AssetMetadata;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.Charset;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder(alphabetic=true)
public class DDO extends AbstractModel implements FromJsonToModel {

    static final Logger log= LogManager.getLogger(DDO.class);

    private static final String DDO_PROOF_TYPE= "DDODataSHA3Signature";
    private static final String UUID_PROOF_TYPE= "UUIDSignature";

    private static final String MODEL_CHARSET= "UTF-8";

    @JsonProperty("@context")
    public String context;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    @JsonProperty
    public Date created;

    public AssetMetadata metadata= null;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    @JsonProperty
    public Date updated;


    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonPropertyOrder(alphabetic=true)
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

    public DID generateDID(String address)    throws DID.DIDGenerationException, DID.DIDFormatException {
        String idHash= UUID.randomUUID().toString().replaceAll("-", "");
        log.debug("Id generated: " + idHash);
        this.proof= new Proof(UUID_PROOF_TYPE, address, idHash);
        this.id= DID.getFromHash(idHash);
        return this.id;
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
