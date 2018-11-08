package com.oceanprotocol.squid.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.oceanprotocol.squid.helpers.HttpHelper;
import com.oceanprotocol.squid.models.DDO;
import com.oceanprotocol.squid.models.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class AquariusDto {

    private static final Logger log = LogManager.getLogger(AquariusDto.class);

    private static final String DDO_URI = "/api/v1/aquarius/assets/ddo";
    private static String DDO_ENDPOINT;

    private String url;

    public static AquariusDto getInstance(String url)    {
        log.debug("Getting Aquarius instance: " + url);
        return new AquariusDto(url);
    }

    private AquariusDto(String url) {
        this.url= url.replaceAll("/$", "");
        this.DDO_ENDPOINT= this.url + DDO_URI;
    }

    public DDO createDDO(DDO ddo) throws Exception {
        log.debug("Creating DDO: " + ddo.id);

        HttpResponse response= HttpHelper.httpClientPost(
                this.DDO_ENDPOINT, new ArrayList<>(), ddo.toJson());
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
        return getDDO(this.DDO_ENDPOINT + "/" +  id);

    }


    public boolean updateDDO(String id, DDO ddo) throws Exception {
        HttpResponse response= HttpHelper.httpClientPut(
                this.DDO_ENDPOINT + "/" + id, new ArrayList<>(), ddo.toJson());

        if (response.getStatusCode() == 200 || response.getStatusCode() == 201)    {
            return true;
        }
        throw new Exception("Unable to update DDO: " + response.toString());
    }

//    public ArrayList<DDO> searchDDO(String query) throws Exception  {
//        HttpResponse response= HttpHelper.httpClientGet(this.DDO_ENDPOINT);
//    }

}
