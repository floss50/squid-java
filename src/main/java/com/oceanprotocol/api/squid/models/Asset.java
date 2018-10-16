package com.oceanprotocol.api.squid.models;

/*
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
*/

import java.util.Date;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class Asset {

    public enum assetTypes {dataset, algorithm, container, workflow, other};

    private String assetId;

    public Asset(String assetId)   {
        this.assetId= assetId;
    }


    //@JsonIgnoreProperties(ignoreUnknown = true)
    public class Base {
        /**
         * name	Text	Yes	Descriptive name of the Asset
         * type	Text	Yes	Type of the Asset. Helps to filter by kind of asset, initially ("dataset", "algorithm", "container", "workflow", "other")
         * description	Text	No	Details of what the resource is. For a data set explain what the data represents and what it can be used for
         * dateCreated	DateTime	Yes	The date on which was created or was added
         * size	Text	Yes	Size of the asset (e.g. 18mb). In the absence of a unit (mb, kb etc.), KB will be assumed
         * author	Text	Yes	Name of the entity generating this data (e.g. Tfl, Disney Corp, etc.)
         * license	Text	Yes	Short name referencing to the license of the asset (e.g. Public Domain, CC-0, CC-BY, No License Specified, etc. ). If it's not specified, the following value will be added: "No License Specifiedified"
         * copyrightHolder	Text	No	The party holding the legal copyright. Empty by default
         * encoding	Text	No	File encoding (e.g. UTF-8)
         * compression	Text	No	File compression (e.g. no, gzip, bzip2, etc)
         * contentType	Text	Yes	File format if applicable
         * workExample	Text	No	Example of the concept of this asset. This example is part of the metadata, not an external link.
         * contentUrls	Text	Yes	List of content urls resolving the ASSET files
         * links	Text	No	Mapping of links for data samples, or links to find out more information. The key represents the topic of the link, the value is the proper link
         * inLanguage	Text	No	The language of the content or performance or used in an action. Please use one of the language codes from the IETF BCP 47 standard
         * tags	Text	No	Keywords or tags used to describe this content. Multiple entries in a keywords list are typically delimited by commas. Empty by default
         * price	Number	Yes	Price of the asset. If not specified would be 0.
         */
/*

        @JsonProperty
        public String name;

        @JsonProperty
        public String type;

        @JsonProperty
        public String description;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
        @JsonProperty
        public Date dateCreated;

        @JsonProperty
        public String size;

        @JsonProperty
        public String author;

        @JsonProperty
        public String license;

        @JsonProperty
        public String copyrightHolder;

        @JsonProperty
        public String encoding;

        @JsonProperty
        public String compression;

        @JsonProperty
        public String contentType;

        @JsonProperty
        public String workExample;

        @JsonProperty
        public String contentUrls;

        @JsonProperty
        public String links;

        @JsonProperty
        public String inLanguage;

        @JsonProperty
        public String tags;

        @JsonProperty
        public String price;
*/

    }

}
