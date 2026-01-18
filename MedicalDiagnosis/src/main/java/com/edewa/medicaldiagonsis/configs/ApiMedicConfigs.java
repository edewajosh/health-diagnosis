package com.edewa.medicaldiagonsis.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "apimedic")
public record ApiMedicConfigs(String baseUrl, String authUrl, String username, String password, String language, boolean mockEnabled) {
}
