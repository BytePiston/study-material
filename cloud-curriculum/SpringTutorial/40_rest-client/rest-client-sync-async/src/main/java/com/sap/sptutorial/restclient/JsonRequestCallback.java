package com.sap.sptutorial.restclient;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.http.MediaType;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.web.client.AsyncRequestCallback;
import org.springframework.web.client.RequestCallback;

public class JsonRequestCallback implements RequestCallback, AsyncRequestCallback {
    @Override
    public void doWithRequest(ClientHttpRequest request)
            throws IOException {
        request.getHeaders().setAccept(
                Arrays.asList(MediaType.APPLICATION_JSON));

    }

    @Override
    public void doWithRequest(AsyncClientHttpRequest request)
            throws IOException {
        request.getHeaders().setAccept(
                Arrays.asList(MediaType.APPLICATION_JSON)); 
    }
}