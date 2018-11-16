package com.oceanprotocol.squid.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oceanprotocol.squid.helpers.HttpHelper;
import com.oceanprotocol.squid.models.AbstractModel;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.HttpResponse;
import com.oceanprotocol.squid.models.aquarius.SearchQuery;
import com.oceanprotocol.squid.models.service.Condition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

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

    public DDO createDDO(DDO ddo) throws Exception {
        log.debug("Creating DDO: " + ddo.id);

        HttpResponse response= HttpHelper.httpClientPost(
                this.ddoEndpoint, new ArrayList<>(), ddo.toJson());
        if (response.getStatusCode() != 201)    {
            throw new Exception("Unable to create DDO: " + response.toString());
        }
        return DDO.fromJSON(new TypeReference<DDO>() {}, response.getBody());
    }

    public DDO getDDO(String url) throws Exception {
        log.debug("Getting DDO: " + url);

        HttpResponse response= HttpHelper.httpClientGet(url);

        if (response.getStatusCode() != 200)    {
            throw new Exception("Unable to get DDO: " + response.toString());
        }
        return DDO.fromJSON(new TypeReference<DDO>() {}, response.getBody());
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

    public ArrayList<DDO> searchDDO(String param, int offset, int page) throws Exception  {
        String url= this.ddoEndpoint + "/query?text=" + param + "&page=" + page + "&offset=" + offset;

        HttpResponse response= HttpHelper.httpClientGet(url);

        if (response.getStatusCode() != 200)    {
            throw new Exception("Unable to search for DDO's: " + response.toString());
        }

        return AbstractModel
                .getMapperInstance()
                .readValue(response.getBody(), new TypeReference<ArrayList<DDO>>() {});

    }

    public ArrayList<DDO> searchDDO(SearchQuery searchQuery) throws Exception  {
        HttpResponse response= HttpHelper.httpClientPost(
                this.ddoEndpoint + "/query", new ArrayList<>(), searchQuery.toJson()
        );

        if (response.getStatusCode() != 200)    {
            throw new Exception("Unable to search for DDO's: " + response.toString());
        }

        return AbstractModel
                .getMapperInstance()
                .readValue(response.getBody(), new TypeReference<ArrayList<DDO>>() {});

    }

}
