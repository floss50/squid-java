package com.oceanprotocol.squid.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.oceanprotocol.squid.exceptions.DDOException;
import com.oceanprotocol.squid.helpers.HttpHelper;
import com.oceanprotocol.squid.models.AbstractModel;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.HttpResponse;
import com.oceanprotocol.squid.models.aquarius.SearchQuery;
import org.apache.commons.httpclient.HttpException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class AquariusDto {

    private static final Logger log = LogManager.getLogger(AquariusDto.class);

    private static final String DDO_URI = "/api/v1/aquarius/assets/ddo";
    private String ddoEndpoint;

    private String url;

    public static AquariusDto getInstance(String url)    {
        log.debug("Getting Aquarius instance: " + url);
        return new AquariusDto(url);
    }

    private AquariusDto(String url) {
        this.url= url.replaceAll("/$", "");
        this.ddoEndpoint = this.url + DDO_URI;
    }

    public String getDdoEndpoint() {
        return ddoEndpoint;
    }

    public DDO createDDO(DDO ddo) throws DDOException {

        log.debug("Creating DDO: " + ddo.id);

        try {

            HttpResponse response= HttpHelper.httpClientPost(
                    this.ddoEndpoint, new ArrayList<>(), ddo.toJson());

            if (response.getStatusCode() != 201)    {
                throw new DDOException("Unable to create DDO: " + response.toString());
            }

            return DDO.fromJSON(new TypeReference<DDO>() {}, response.getBody());

        } catch (Exception e){
            throw new DDOException("Error building DDO from JSON", e);
        }

    }

    public DDO getDDO(String url) throws DDOException {

        log.debug("Getting DDO: " + url);
        HttpResponse response;

        try {
             response = HttpHelper.httpClientGet(url);
        } catch (HttpException e) {
            throw new DDOException("Unable to get DDO", e);
        }

        if (response.getStatusCode() != 200)    {
            throw new DDOException("Unable to get DDO: " + response.toString());
        }
        try {
            return DDO.fromJSON(new TypeReference<DDO>() {}, response.getBody());
        } catch (Exception e){
            throw new DDOException("Error building DDO from JSON", e);
        }
    }

    public DDO getDDOUsingId(String id) throws Exception {
        return getDDO(this.ddoEndpoint + "/" +  id);

    }


    public boolean updateDDO(String id, DDO ddo) throws Exception {
        HttpResponse response= HttpHelper.httpClientPut(
                this.ddoEndpoint + "/" + id, new ArrayList<>(), ddo.toJson());

        if (response.getStatusCode() == 200 || response.getStatusCode() == 201)    {
            return true;
        }
        throw new Exception("Unable to update DDO: " + response.toString());
    }

    public ArrayList<DDO> searchDDO(String param, int offset, int page) throws DDOException  {

        String url= this.ddoEndpoint + "/query?text=" + param + "&page=" + page + "&offset=" + offset;
        HttpResponse response;

        try {
            response= HttpHelper.httpClientGet(url);
        } catch (HttpException e) {
            throw new DDOException("Unable to get DDO: ", e);
        }

        if (response.getStatusCode() != 200)    {
            throw new DDOException("Unable to search for DDO's: " + response.toString());
        }

        try {
            return AbstractModel
                    .getMapperInstance()
                    .readValue(response.getBody(), new TypeReference<ArrayList<DDO>>() {});
        } catch (IOException e) {
            throw new DDOException("Unable to search for DDO's: ", e);
        }

    }

    public ArrayList<DDO> searchDDO(SearchQuery searchQuery) throws DDOException {

        HttpResponse response;

        try {
            response = HttpHelper.httpClientPost(
                     this.ddoEndpoint + "/query", new ArrayList<>(), searchQuery.toJson());
        } catch (Exception e) {
            throw new DDOException("Unable to get DDO", e);
        }

        if (response.getStatusCode() != 200)    {
            throw new DDOException("Unable to search for DDO's: " + response.toString());
        }

        try {
            return AbstractModel
                    .getMapperInstance()
                    .readValue(response.getBody(), new TypeReference<ArrayList<DDO>>() {});
        } catch (IOException e) {
            throw new DDOException("Unable to search for DDO's", e);
        }

    }

}
