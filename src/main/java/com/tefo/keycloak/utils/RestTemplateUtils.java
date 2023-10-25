package com.tefo.keycloak.utils;

import com.tefo.keycloak.constants.RestEndpoints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class RestTemplateUtils {

    private RestTemplateUtils() {
    }

    private static final String IDENTITY_SERVICE_HOST = "IDENTITY_SERVICE_HOST";

    public static void activateUser(String id) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.exchange(
                    RestEndpoints.HTTP + System.getenv(IDENTITY_SERVICE_HOST) + RestEndpoints.USERS + "/activate/" + id,
                    HttpMethod.PUT,
                    null,
                    Void.class
            );
        } catch (HttpClientErrorException e) {
            log.error("Can't process update user status to ACTIVE " + e.getMessage());
        }
    }

    public static void setUserStatus(String userId, String errorMessage, String previousUserStatus) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.exchange(
                    RestEndpoints.HTTP + System.getenv(IDENTITY_SERVICE_HOST) + RestEndpoints.KEYCLOAK_SERVICE
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
}
