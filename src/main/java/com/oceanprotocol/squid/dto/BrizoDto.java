package com.oceanprotocol.squid.dto;

import com.oceanprotocol.squid.helpers.HttpHelper;
import com.oceanprotocol.squid.helpers.StringsHelper;
import com.oceanprotocol.squid.models.HttpResponse;
import com.oceanprotocol.squid.models.brizo.InitializeAccessSLA;
import com.oceanprotocol.squid.models.service.Service;
import org.apache.commons.httpclient.HttpException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * Calls a Brizo´s endpoint to download an asset
     * @param serviceEndpoint
     * @param consumerAddress
     * @param serviceAgreementId
     * @param url
     * @return An InputStream with the binary content of the asset
     * @throws IOException
     * @throws URISyntaxException
     */
    public static InputStream consumeUrl(String serviceEndpoint, String consumerAddress, String serviceAgreementId, String url) throws IOException, URISyntaxException {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(Service.CONSUMER_ADDRESS_PARAM, consumerAddress);
        parameters.put(Service.SERVICE_AGREEMENT_PARAM, serviceAgreementId);
        parameters.put(Service.URL_PARAM, url);

        String endpoint = StringsHelper.format(serviceEndpoint, parameters);

        log.debug("Consuming URL[" + url + "]: for service Agreement " + serviceAgreementId);

        return HttpHelper.downloadResource(endpoint);

    }
}
