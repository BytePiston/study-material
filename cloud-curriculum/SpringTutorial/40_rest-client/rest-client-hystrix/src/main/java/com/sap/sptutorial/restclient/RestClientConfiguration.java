package com.sap.sptutorial.restclient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RestClientConfiguration {

    private final class RestTemplateErrorHandler
            implements ResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse response)
                throws IOException {
            return !response.getStatusCode().is2xxSuccessful();
        }

        @Override
        public void handleError(ClientHttpResponse response)
                throws IOException {
            log.warn("Http error");
            log.debug(response.getStatusText());
            log.debug(StreamUtils.copyToString(response.getBody(),
                    Charset.defaultCharset()));
        }
    }

    @Bean
    public RestTemplate syncRestTemplate(ConnectionSettings settings) {
        RestTemplate restTemplate = buildRestTemplate(settings.getProxy(), RestTemplate::new, RestTemplate::new);
        restTemplate.setErrorHandler(new RestTemplateErrorHandler());
        return restTemplate;
    }
    
    private <T> T buildRestTemplate(ConnectionSettings.Proxy proxySettings, Supplier<T> noArgsCnstr, Function<SimpleClientHttpRequestFactory, T> cnstr) {
        T restTemplate;

        if (proxySettings != null) {
            SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
                    proxySettings.getHost(), proxySettings.getPort()));
            clientHttpRequestFactory.setProxy(proxy);
            restTemplate = cnstr.apply(clientHttpRequestFactory);
        } else {
            restTemplate = noArgsCnstr.get();
        }
        return restTemplate;  
    }
}
