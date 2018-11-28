package com.oceanprotocol.squid.dto;

import com.oceanprotocol.squid.helpers.HttpHelper;
import com.oceanprotocol.squid.models.HttpResponse;
import com.oceanprotocol.squid.models.brizo.InitializeAccessSLA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class BrizoDto {

    private static final Logger log = LogManager.getLogger(BrizoDto.class);


    public static boolean initializeAccessServiceAgreement(String url, InitializeAccessSLA payload)  {
        log.debug("Initializing SLA[" + payload.serviceAgreementId + "]: " + url);

        try {
            String payloadJson= payload.toJson();
            log.debug(payloadJson);

            HttpResponse response = HttpHelper.httpClientPost(
                    url, new ArrayList<>(), payloadJson);

            if (response.getStatusCode() != 201) {
                log.error("Unable to Initialize SLA: " + response.toString());
                return false;
            }
        } catch (Exception e)   {
            log.error("Exception Initializing SLA: " + e.getMessage());

            return false;
        }
        return true;
    }
}
