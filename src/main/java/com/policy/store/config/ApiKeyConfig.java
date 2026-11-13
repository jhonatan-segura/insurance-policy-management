package com.policy.store.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiKeyConfig {

    @Value("${app.security.api-key}")
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }
}