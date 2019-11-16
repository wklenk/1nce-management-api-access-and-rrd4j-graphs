package org.klenk.connectivity.iot.oncemanagementapi.configuration;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, OkHttpClient.Builder okHttpClientBuilder) {

        OkHttpClient client = okHttpClientBuilder
                .build();

        return new RestTemplate(new OkHttp3ClientHttpRequestFactory(client));
    }
}