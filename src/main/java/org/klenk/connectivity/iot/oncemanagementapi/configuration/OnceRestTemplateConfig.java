/*
  1nce-management-api-access-and-rrd4j-graphs - Simple solution to read out NB-IoT data traffic consumption from
  the 1NCE management API, store it in a round-robin database and show the result in a graph.

  Copyright (C) 2019  Wolfgang Klenk <wolfgang.klenk@gmail.com>

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <https://www.gnu.org/licenses/>.

*/
package org.klenk.connectivity.iot.oncemanagementapi.configuration;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.klenk.connectivity.iot.oncemanagementapi.service.OnceAccessTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@Configuration
public class OnceRestTemplateConfig {

    @Bean
    public OnceRestTemplate onceRestTemplate(
            OkHttpClient.Builder okHttpClientBuilder,
            OnceAccessTokenService accessTokenService ) {

        JwtAuthenticator authenticator = new JwtAuthenticator(accessTokenService);

        OkHttpClient client = okHttpClientBuilder
                .addInterceptor(authenticator)
                .authenticator(authenticator)
                .build();

        return new OnceRestTemplate(new OkHttp3ClientHttpRequestFactory(client));
    }

    public class OnceRestTemplate extends RestTemplate {

        public OnceRestTemplate(ClientHttpRequestFactory requestFactory) {
            super(requestFactory);
        }
    }

    private static class JwtAuthenticator implements Authenticator, Interceptor {

        private final OnceAccessTokenService accessTokenService;

        public JwtAuthenticator(OnceAccessTokenService accessTokenService) {
            this.accessTokenService = accessTokenService;
        }

        // Implementations authenticate by returning a follow-up request that includes an authorization header,
        // or they may decline the challenge by returning null. In this case the unauthenticated response will
        // be returned to the caller that triggered it.
        @Override
        public Request authenticate(Route route, Response response) throws IOException {
            log.info("route={} response={}", route, response);

            if (response.code() == HttpStatus.UNAUTHORIZED.value()) {
                try {
                    String accessToken;
                    if (response.request().header("Authorization") != null) {
                        log.info("Request used this Authorization header: {}", response.request().header("Authorization"));

                        accessToken = accessTokenService.getNewAccessToken();
                    } else {
                        log.info("Request did NOT use an Authorization header.");
                        accessToken = accessTokenService.getNewAccessToken();
                    }

                    if (accessToken == null) {
                        log.warn("Could not get access token for 1nce Management API.");
                        return null;
                    }

                    return response.request().newBuilder()
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                            .build();
                }
                catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }

            log.info("Response code not handled: {}", response.code());
            return null;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            String currentAccessToken = accessTokenService.getCurrentAccessToken();
            if (currentAccessToken != null) {
                return chain.proceed(chain.request().newBuilder()
                        .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + currentAccessToken)
                        .build());
            }

            return chain.proceed(chain.request());
        }
    }
}