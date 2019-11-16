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
package org.klenk.connectivity.iot.oncemanagementapi.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.klenk.connectivity.iot.oncemanagementapi.configuration.OnceAuthenticationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

//
// An authentication service for using the Management API of 1nce.com.
//
// From https://1nce.com/en/help-center/tutorials-documentations/api-functionality/
//
// The usage of 1NCE REST API via the customer’s external system demands for an authentication first.
// Therefore, the authentication via OAuth2 with the same credentials being used as for the 1NCE Customer Portal
// (e-mail address and password) is required. As an “API User” the authentication method with client_id and
// client_secret is required.
//
// The OAuth2 authentication is necessary to get a token with credentials of the 1NCE Customer Portal with the following workflow:
//
// * Send your credential base64 encoded to the authentication server.
// * Get a response including an UUID for authentication.
// * Use the UUID to authenticate REST requests.
//
// The UUID for authentication has a validity period of 240 minutes.
// Notice: If the 240 minutes have expired the authentication process needs to be repeated.
//
// Send the authentication request via POST to
// https://api.1nce.com/management-api/oauth/token
//
// Request Header
//
// "Content-Type": "application/x-www-form-urlencoded",
// "Authorization": "Basic <base64 encoded username:password>"
//
// Request Body
//
// {
//   "grant_type": "client_credentials"
// }
//
// Response
//
// {
//   "access_token": "6ba7b810-9dad-11d1-80b4-00c04fd43XXX", // this is the UUID token you want to use for authentication
//   "token_type": "bearer",
//   "expires_in": 3599,
//   "scope": "all",
//   "appToken": "<application_token>", // not relevant in this context
//   "userId": <id>,
//   "orgId": 4321 // example
// }
//
@Slf4j
@Service
@EnableConfigurationProperties(OnceAuthenticationProperties.class)
public class OnceAccessTokenService {

    private final RestTemplate restTemplate;
    private final OnceAuthenticationProperties authenticationProperties;

    private Tokens tokens;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tokens {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("expires_in")
        private Long expiresIn;

        @JsonProperty("scope")
        private String scope;

        @JsonProperty("appToken")
        private String appToken;
    }

    public OnceAccessTokenService(RestTemplate restTemplate, OnceAuthenticationProperties authenticationProperties) {
        this.restTemplate = restTemplate;
        this.authenticationProperties = authenticationProperties;
    }

    public String getNewAccessToken() {

        tokens = getAccessTokenByClientCredentials();
        if (tokens != null) {
            return tokens.getAccessToken();
        }

        return null;
    }

    public String getCurrentAccessToken() {
        if (tokens != null) {
            return tokens.accessToken;
        }

        return null;
    }

    private Tokens getAccessTokenByClientCredentials() {

        log.info("Requesting a new Access Token by client credentials");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(authenticationProperties.getClientId(), authenticationProperties.getClientSecret());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type","client_credentials");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<Tokens> response =
                restTemplate.exchange(authenticationProperties.getTokenEndpoint(),
                        HttpMethod.POST,
                        entity,
                        Tokens.class);

        log.info("Token endpoint returned status {} and tokens {}", response.getStatusCode(), response.getBody());

        return response.getBody();
    }
}
