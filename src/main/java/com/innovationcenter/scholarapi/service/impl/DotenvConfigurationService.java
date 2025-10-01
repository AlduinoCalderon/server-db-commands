package com.innovationcenter.scholarapi.service.impl;

import com.innovationcenter.scholarapi.service.ConfigurationService;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of ConfigurationService for environment configuration.
 * Handles environment configuration and API key management.
 */
public class DotenvConfigurationService implements ConfigurationService {
    
    private static final Logger logger = LoggerFactory.getLogger(DotenvConfigurationService.class);
    private static final String DEFAULT_API_KEY = "demo_api_key_for_testing";
    
    private Dotenv dotenv;
    private String apiKey;
    
    public DotenvConfigurationService() {
        loadConfiguration();
    }
    
    @Override
    public boolean loadConfiguration() {
        try {
            dotenv = Dotenv.configure()
                    .directory("./")
                    .filename(".env")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();
            
            apiKey = dotenv.get("SERP_API_KEY");
            
            // Log configuration status (masking sensitive data)
            if (apiKey != null && !apiKey.isEmpty()) {
                String maskedKey = apiKey.length() > 8 ? 
                    "***" + apiKey.substring(apiKey.length() - 5) : "***";
                logger.info("API key from .env file: {}", maskedKey);
                logger.info("API key configured successfully from environment.");
            } else {
                logger.warn("No API key found in .env file, using default configuration");
                apiKey = DEFAULT_API_KEY;
            }
            
            return true;
        } catch (Exception e) {
            logger.error("Failed to load configuration: {}", e.getMessage());
            apiKey = DEFAULT_API_KEY;
            return false;
        }
    }
    
    @Override
    public String getApiKey() {
        return apiKey;
    }
    
    @Override
    public boolean isConfigurationValid() {
        return apiKey != null && !apiKey.isEmpty() && !apiKey.equals(DEFAULT_API_KEY);
    }
    
    @Override
    public String getProperty(String key) {
        return dotenv != null ? dotenv.get(key) : null;
    }
}