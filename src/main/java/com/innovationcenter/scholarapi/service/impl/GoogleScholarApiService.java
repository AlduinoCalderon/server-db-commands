package com.innovationcenter.scholarapi.service.impl;

import com.innovationcenter.scholarapi.model.Author;
import com.innovationcenter.scholarapi.model.AuthorSearchResult;
import com.innovationcenter.scholarapi.service.ApiService;
import com.innovationcenter.scholarapi.service.ConfigurationService;
import com.innovationcenter.scholarapi.service.JsonParser;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ApiService for Google Scholar API integration.
 * Uses dependency injection for configuration and parsing services.
 */
public class GoogleScholarApiService implements ApiService {
    
    private static final Logger logger = LoggerFactory.getLogger(GoogleScholarApiService.class);
    private static final String SCHOLAR_API_BASE_URL = "https://serpapi.com/search.json";
    
    private final ConfigurationService configurationService;
    private final JsonParser jsonParser;
    private final CloseableHttpClient httpClient;
    
    /**
     * Constructor with dependency injection.
     */
    public GoogleScholarApiService(ConfigurationService configurationService, JsonParser jsonParser) {
        this.configurationService = configurationService;
        this.jsonParser = jsonParser;
        this.httpClient = HttpClients.createDefault();
    }
    
    @Override
    public List<AuthorSearchResult> searchAuthors(String query) throws IOException {
        if (!isConfigured()) {
            throw new IllegalStateException("API service is not properly configured");
        }
        
        String searchUrl = buildSearchUrl(query);
        logger.info("Making API request to: {}", maskApiKey(searchUrl));
        
        try (CloseableHttpResponse response = makeHttpRequest(searchUrl)) {
            String jsonResponse = EntityUtils.toString(response.getEntity());
            return parseSearchResponse(jsonResponse);
        }
    }
    
    @Override
    public boolean isConfigured() {
        return configurationService.isConfigurationValid();
    }
    
    @Override
    public String getServiceName() {
        return "Google Scholar API";
    }
    
    /**
     * Build search URL for Google Scholar API.
     */
    private String buildSearchUrl(String query) {
        try {
            String encodedQuery = URLEncoder.encode("author:\"" + query + "\"", StandardCharsets.UTF_8.toString());
            return String.format("%s?engine=google_scholar&q=%s&api_key=%s&num=10",
                    SCHOLAR_API_BASE_URL, encodedQuery, configurationService.getApiKey());
        } catch (Exception e) {
            throw new RuntimeException("Failed to build search URL", e);
        }
    }
    
    /**
     * Make HTTP request to the API.
     */
    private CloseableHttpResponse makeHttpRequest(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", "GoogleScholarAPI/1.0");
        return httpClient.execute(httpGet);
    }
    
    /**
     * Parse JSON response and convert to AuthorSearchResult list.
     */
    private List<AuthorSearchResult> parseSearchResponse(String jsonResponse) {
        List<AuthorSearchResult> results = new ArrayList<>();
        
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            
            if (!jsonObject.has("organic_results")) {
                logger.warn("No organic_results found in API response");
                return results;
            }
            
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");
            logger.info("Parsed {} authors from search response", organicResults.length());
            
            for (int i = 0; i < organicResults.length(); i++) {
                JSONObject result = organicResults.getJSONObject(i);
                Author author = jsonParser.parseAuthor(result);
                
                if (author != null) {
                    AuthorSearchResult searchResult = new AuthorSearchResult();
                    searchResult.setAuthor(author);
                    searchResult.setRank(i + 1);
                    searchResult.setSource("Google Scholar");
                    results.add(searchResult);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error parsing search response: {}", e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Mask API key in URL for logging purposes.
     */
    private String maskApiKey(String url) {
        return url.replaceAll("api_key=[^&]*", "api_key=***");
    }
    
    /**
     * Clean up resources.
     */
    public void close() throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
    }
}