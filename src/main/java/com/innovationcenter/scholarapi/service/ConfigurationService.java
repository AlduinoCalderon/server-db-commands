package com.innovationcenter.scholarapi.service;

/**
 * Interface for configuration management.
 * Defines contract for environment and configuration operations.
 */
public interface ConfigurationService {
    
    /**
     * Load configuration from environment.
     * @return true if loaded successfully, false otherwise
     */
    boolean loadConfiguration();
    
    /**
     * Get API key for external services.
     * @return API key string
     */
    String getApiKey();
    
    /**
     * Check if configuration is valid and complete.
     * @return true if valid, false otherwise
     */
    boolean isConfigurationValid();
    
    /**
     * Get configuration property by key.
     * @param key Property key
     * @return Property value or null if not found
     */
    String getProperty(String key);
    
    /**
     * Get configuration property by key with default value.
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default value
     */
    default String getPropertyOrDefault(String key, String defaultValue) {
        String value = getProperty(key);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }
}