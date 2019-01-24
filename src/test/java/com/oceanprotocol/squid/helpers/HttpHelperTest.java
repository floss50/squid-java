package com.oceanprotocol.squid.helpers;

import com.oceanprotocol.squid.models.HttpResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpHelperTest {

    @Test
    public void httpClientGenericMethod() throws HttpException, UnsupportedEncodingException, IOException {

        String payload= "{'message': 'hi there'}";
        ArrayList<NameValuePair> list= new ArrayList<>();
        list.add(new NameValuePair("key", "value"));

        HttpClient client= mock(HttpClient.class);
        EntityEnclosingMethod method= mock(PostMethod.class);

        when(client.executeMethod(method)).thenReturn(1);

        when(method.getStatusCode()).thenReturn(201);
        when(method.getResponseBodyAsStream()).thenReturn(new ByteArrayInputStream( "".getBytes() ));
        when(method.getResponseCharSet()).thenReturn("UTF-8");
        when(method.getResponseContentLength()).thenReturn(0L);

        HttpResponse response= HttpHelper.httpClientGenericMethod(client, method, list, payload);

        assertEquals(201, response.getStatusCode());
    }


    @Test
    public void httpClientGet() throws HttpException, IOException {
        HttpClient client= mock(HttpClient.class);
        GetMethod method= mock(GetMethod.class);

        when(client.executeMethod(method)).thenReturn(1);

        when(method.getStatusCode()).thenReturn(200);
        when(method.getResponseBodyAsStream()).thenReturn(new ByteArrayInputStream( "".getBytes() ));
        when(method.getResponseCharSet()).thenReturn("UTF-8");
        when(method.getResponseContentLength()).thenReturn(0L);


        HttpResponse response= HttpHelper.httpClientGet(client, method);
        assertEquals(200, response.getStatusCode());
    }


}