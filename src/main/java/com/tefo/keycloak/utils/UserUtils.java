package com.tefo.keycloak.utils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserUtils {

    private UserUtils() {
    }

    private static final String USER_ID_ATTRIBUTE = "userId";
    public static final String USER_STATUS_ATTRIBUTE = "status";

    public static String getUserIdFromAttributes(Map<String, List<String>> attributes) {
        return getUserAttribute(attributes, USER_ID_ATTRIBUTE);
    }

    public static String getUserStatusFromAttributes(Map<String, List<String>> attributes) {
        return getUserAttribute(attributes, USER_STATUS_ATTRIBUTE);
    }

    private static String getUserAttribute(Map<String, List<String>> attributes, String attribute) {
        return Optional.ofNullable(attributes.get(attribute))
                .flatMap(list -> list.stream().findFirst())
                .orElse(null);
    }
}
