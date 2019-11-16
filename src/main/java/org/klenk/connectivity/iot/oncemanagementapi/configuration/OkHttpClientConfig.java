package org.klenk.connectivity.iot.oncemanagementapi.configuration;

import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
@EnableConfigurationProperties(OkHttpClientProperties.class)
public class OkHttpClientConfig {

    @Bean
    public OkHttpClient.Builder okHttpClient(OkHttpClientProperties properties) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (properties.getProxy() != null) {
            builder = builder.proxy(
                    new Proxy(
                            Proxy.Type.HTTP,
                            new InetSocketAddress(properties.getProxy().getHost(), properties.getProxy().getPort()))
            );
        }

        return builder;
    }
}
