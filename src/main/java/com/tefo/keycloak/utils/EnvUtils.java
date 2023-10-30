package com.tefo.keycloak.utils;

public class EnvUtils {
    public static String getEnvProperty(String serviceHost, String realmId) {
        String envName;
        if (realmId.contains("-")) {
            String[] realmFullName = realmId.split("-");
            envName = "_" + realmFullName[realmFullName.length - 1];
        } else {
            envName = "";
        }
        return System.getenv(serviceHost + envName.toUpperCase());
    }
}
