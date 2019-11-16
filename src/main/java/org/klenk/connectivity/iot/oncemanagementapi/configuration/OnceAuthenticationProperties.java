package org.klenk.connectivity.iot.oncemanagementapi.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties("1nce")
@Data
@ConstructorBinding
public class OnceAuthenticationProperties {

    private String clientId;
    private String clientSecret;
    private String tokenEndpoint;
}
