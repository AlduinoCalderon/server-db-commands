package com.innovationcenter.scholarapi;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Debug utility to test .env file loading
 */
public class DebugEnv {
    public static void main(String[] args) {
        System.out.println("=== Environment Variable Debug ===");
        
        // Test 1: System environment variable
        String systemEnvKey = System.getenv("SERPAPI_KEY");
        System.out.println("System.getenv(\"SERPAPI_KEY\"): " + 
            (systemEnvKey != null ? "***" + systemEnvKey.substring(Math.max(0, systemEnvKey.length() - 4)) : "null"));
        
        // Test 2: Dotenv library
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            String dotenvKey = dotenv.get("SERPAPI_KEY");
            System.out.println("Dotenv.get(\"SERPAPI_KEY\"): " + 
                (dotenvKey != null ? "***" + dotenvKey.substring(Math.max(0, dotenvKey.length() - 4)) : "null"));
            
            System.out.println("Working directory: " + System.getProperty("user.dir"));
            System.out.println(".env file should be at: " + System.getProperty("user.dir") + "/.env");
            
        } catch (Exception e) {
            System.out.println("Error loading dotenv: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Test 3: Check what the ScholarApiController static block would see
        System.out.println("\n=== ScholarApiController Logic Test ===");
        Dotenv dotenv = null;
        try {
            dotenv = Dotenv.configure().ignoreIfMissing().load();
        } catch (Exception e) {
            System.out.println("Dotenv load failed: " + e.getMessage());
        }
        
        String apiKey = null;
        if (dotenv != null) {
            apiKey = dotenv.get("SERPAPI_KEY");
            System.out.println("Dotenv loaded successfully, API key: " + 
                (apiKey != null ? "Found (***" + apiKey.substring(Math.max(0, apiKey.length() - 4)) + ")" : "Not found"));
        }
        if (apiKey == null || apiKey.trim().isEmpty()) {
            apiKey = System.getenv("SERPAPI_KEY");
            System.out.println("Fallback to system env, API key: " + 
                (apiKey != null ? "Found (***" + apiKey.substring(Math.max(0, apiKey.length() - 4)) + ")" : "Not found"));
        }
        
        String finalKey = apiKey != null ? apiKey : "YOUR_SERPAPI_KEY";
        System.out.println("Final API key value: " + 
            (finalKey.equals("YOUR_SERPAPI_KEY") ? "PLACEHOLDER" : "CONFIGURED (***" + finalKey.substring(Math.max(0, finalKey.length() - 4)) + ")"));
        
        // Test isApiKeyConfigured logic
        boolean isConfigured = finalKey != null && !finalKey.trim().isEmpty() && 
                              !"YOUR_SERPAPI_KEY".equals(finalKey) && !finalKey.startsWith("YOUR_");
        System.out.println("isApiKeyConfigured() would return: " + isConfigured);
    }
}