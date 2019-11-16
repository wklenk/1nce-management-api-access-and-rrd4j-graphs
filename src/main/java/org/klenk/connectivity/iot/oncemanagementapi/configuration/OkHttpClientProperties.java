package org.klenk.connectivity.iot.oncemanagementapi.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties("ok-http-client")
@Data
@ConstructorBinding
public class OkHttpClientProperties {

    private Proxy proxy;

    @Data
    static class Proxy {
        String host;
        int port;
    }
}
