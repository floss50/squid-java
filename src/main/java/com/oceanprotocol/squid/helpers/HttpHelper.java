package com.oceanprotocol.squid.helpers;

import com.oceanprotocol.squid.models.HttpResponse;
import org.apache.commons.codec.Charsets;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * HTTP Helper functions
 */
public abstract class HttpHelper {

    protected static final Logger log = LogManager.getLogger(HttpHelper.class);

    private HttpHelper() {
    }

    /**
     * Send a HTTP POST request and return the body
     * @param url url to call
     * @return returned http body
     * @throws HttpException Http error
     */
    public static final String httpClientPostBody(String url) throws HttpException, UnsupportedEncodingException {
        return httpClientGenericMethod(new PostMethod(url), new ArrayList<>(), null).getBody();
    }

    /**
     * Send a HTTP POST request with parameters and return the body
     * @param url url to call
     * @param list parameters
     * @return returned http body
     * @throws HttpException Http error
     */
    public static final String httpClientPostBody(String url, ArrayList<NameValuePair> list) throws HttpException, UnsupportedEncodingException {
        return httpClientGenericMethod(new PostMethod(url), list, null).getBody();
    }

    /**
     * Send a HTTP POST request and return the HttpResponse object
     * @param url url to call
     * @return HttpResponse returned
     * @throws HttpException Http error
     */
    public static final HttpResponse httpClientPost(String url) throws HttpException, UnsupportedEncodingException {
        return httpClientGenericMethod(new PostMethod(url), new ArrayList<>(), null);
    }

    /**
     * Send a HTTP POST request and return the HttpResponse object
     * @param url url to call
     * @return HttpResponse returned
     * @throws HttpException Http error
     */
    public static final HttpResponse httpClientPost(String url, ArrayList<NameValuePair> list, String payload) throws HttpException, UnsupportedEncodingException {
        return httpClientGenericMethod(new PostMethod(url), list, payload);
    }

    /**
     * Send a HTTP PUT request and return the HttpResponse object
     * @param url url to call
     * @return HttpResponse returned
     * @throws HttpException Http error
     */
    public static final HttpResponse httpClientPut(String url, ArrayList<NameValuePair> list, String payload) throws HttpException, UnsupportedEncodingException {
        return httpClientGenericMethod(new PutMethod(url), list, payload);
    }

    /**
     * Send a HTTP request with parameters and return the HttpResponse object
     * @param list parameters
     * @return HttpResponse returned
     * @throws HttpException Http error
     */
    public static final HttpResponse httpClientGenericMethod(EntityEnclosingMethod method, ArrayList<NameValuePair> list, String payload) throws HttpException, UnsupportedEncodingException {

        HttpResponse response;
        StringRequestEntity requestEntity= null;
        HttpClient client = new HttpClient();

        if (null != payload && payload.length() >0) {
            requestEntity = new StringRequestEntity(
                    payload,
                    ContentType.APPLICATION_JSON.toString(),
                    "UTF-8");

            method.setRequestEntity(requestEntity);
        }

        try {
            if (list.size() >0) {
                NameValuePair[] params = new NameValuePair[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    params[i] = list.get(i);
                }

                if (method instanceof PostMethod)
                    ((PostMethod) method).addParameters(params);
            }

            client.executeMethod(method);
            response = new HttpResponse(
                    method.getStatusCode(),
                    IOUtils.toString(method.getResponseBodyAsStream(), Charsets.UTF_8),
                    method.getResponseCharSet(),
                    method.getResponseContentLength()
            );
        } catch (Exception e) {
            log.error("Error in HTTP Method request " + e.getMessage());
            throw new HttpException("Error in HTTP Method request");
        } finally {
            method.releaseConnection();
        }
        return response;
    }

    /**
     * Send a HTTP GET request and return the HttpResponse object
     * @param url url to call
     * @return HttpResponse returned
     * @throws HttpException Http error
     */
    public static final HttpResponse httpClientGet(String url) throws HttpException {

        log.debug("Getting URL: "+ url);

        HttpResponse response;
        HttpClient client = new HttpClient();
        GetMethod getMethod = new GetMethod(url);
        try {

            client.executeMethod(getMethod);
            response = new HttpResponse(
                    getMethod.getStatusCode(),
                    IOUtils.toString(getMethod.getResponseBodyAsStream(), Charsets.UTF_8),
                    getMethod.getResponseCharSet(),
                    getMethod.getResponseContentLength()
            );

        } catch (Exception e) {
            log.error("Error in HTTP GET request " + e.getMessage());
            throw new HttpException("Error in HTTP GET request");
        } finally {
            getMethod.releaseConnection();
        }
        return response;
    }

}
