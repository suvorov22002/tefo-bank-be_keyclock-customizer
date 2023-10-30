package com.tefo.keycloak.utils;

import com.tefo.keycloak.constants.RestEndpoints;
import com.tefo.keycloak.dto.UserResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class RestTemplateUtils {

    private RestTemplateUtils() {
    }

    private static final String IDENTITY_SERVICE_HOST = "IDENTITY_SERVICE_HOST";

    public static void activateUser(String id, String realmId) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.exchange(
                    RestEndpoints.HTTP + EnvUtils.getEnvProperty(IDENTITY_SERVICE_HOST, realmId) + RestEndpoints.USERS + "/activate/" + id,
                    HttpMethod.PUT,
                    null,
                    Void.class
            );
        } catch (HttpClientErrorException e) {
            log.error("Can't process update user status to ACTIVE " + e.getMessage());
        }
    }

    public static void setUserStatus(String userId, String errorMessage, String previousUserStatus, String realmId) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.exchange(
                    RestEndpoints.HTTP + EnvUtils.getEnvProperty(IDENTITY_SERVICE_HOST, realmId) + RestEndpoints.KEYCLOAK_SERVICE
                            + "/user-status/" + userId
                            + "?errorMessage=" + errorMessage
                            + "&previousUserStatus=" + previousUserStatus,
                    HttpMethod.GET,
                    null,
                    Void.class
            );
        } catch (HttpClientErrorException e) {
            log.error("Can't get user status" + e.getMessage());
        }
    }

    public static UserResponseDto getUserById(String userId, String realmId) {
        RestTemplate restTemplate = new RestTemplate();
        UserResponseDto responseDto = new UserResponseDto();
        try {
            responseDto = restTemplate.exchange(
                    RestEndpoints.HTTP + EnvUtils.getEnvProperty(IDENTITY_SERVICE_HOST, realmId) + RestEndpoints.USERS + userId,
                    HttpMethod.GET,
                    null,
                    UserResponseDto.class
            ).getBody();
        } catch (HttpClientErrorException e) {
            log.error("Can't get user" + e.getMessage());
        }
        return responseDto;
    }
}
