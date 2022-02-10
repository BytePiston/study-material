package com.sap.sptutorial.restclient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.Charset;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RestClientConfiguration {
    private ThreadPoolTaskExecutor taskExecutor;
    public static ThreadLocal<String> tenantId = new ThreadLocal<String>();
    
    public RestClientConfiguration() {
        taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setTaskDecorator((runnable) -> {
            final String _tenantId = tenantId.get();
            return () -> {
                tenantId.set(_tenantId);
                runnable.run();
            };
        });
        taskExecutor.initialize();
    }

    private final class RestTemplateErrorHandler implements ResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return !response.getStatusCode().is2xxSuccessful();
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            log.warn("Http error");
            log.debug(response.getStatusText());
            log.debug(StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
        }
    }

    @Bean
    public RestTemplate syncRestTemplate(ConnectionSettings settings) {
        ConnectionSettings.Proxy proxySettings = settings.getProxy();
        RestTemplate restTemplate = null;
        if (proxySettings != null) {
            SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
            Proxy proxy = new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(proxySettings.getHost(), proxySettings.getPort()));
            clientHttpRequestFactory.setProxy(proxy);
            restTemplate = new RestTemplate(clientHttpRequestFactory);
        } else {
            restTemplate = new RestTemplate();
        }
        restTemplate.setErrorHandler(new RestTemplateErrorHandler());
        return restTemplate;
    }

    @Bean
    public AsyncRestTemplate asyncRestTemplate(ConnectionSettings settings) {
        ConnectionSettings.Proxy proxySettings = settings.getProxy();
        AsyncRestTemplate restTemplate = null;
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setTaskExecutor(taskExecutor);
        if (proxySettings != null) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(proxySettings.getHost(), proxySettings.getPort()));
            clientHttpRequestFactory.setProxy(proxy);
            restTemplate = new AsyncRestTemplate(clientHttpRequestFactory);
        } else {
            restTemplate = new AsyncRestTemplate(clientHttpRequestFactory);
        }
        restTemplate.setErrorHandler(new RestTemplateErrorHandler());
        return restTemplate;
    }
}
