package com.innovationcenter.scholarapi.service.impl;

import com.innovationcenter.scholarapi.model.ScholarSearchResponse;
import com.innovationcenter.scholarapi.service.ScholarSearchService;
import com.innovationcenter.scholarapi.service.ConfigurationService;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Implementation of ScholarSearchService using SerpAPI Google Scholar engine.
 * Follows Technical Report specifications for API interaction.
 */
public class SerpApiScholarSearchService implements ScholarSearchService {
    
    private static final Logger logger = Logger.getLogger(SerpApiScholarSearchService.class.getName());
    private static final String BASE_URL = "https://serpapi.com/search.json";
    private static final String ENGINE = "google_scholar";
    
    private final ConfigurationService configurationService;
    private final CloseableHttpClient httpClient;
    
    public SerpApiScholarSearchService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        this.httpClient = HttpClients.createDefault();
    }
    
    @Override
    public ScholarSearchResponse searchArticles(String query, int maxResults) throws IOException {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be null or empty");
        }
        
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = buildUrl("q", encodedQuery, Math.min(maxResults, 20));
        
        logger.info("Searching articles with query: " + query);
        return executeRequest(url);
    }
    
    @Override
    public ScholarSearchResponse searchByAuthor(String authorName, int maxResults) throws IOException {
        if (authorName == null || authorName.trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be null or empty");
        }
        
        String authorQuery = "author:\"" + authorName + "\"";
        String encodedQuery = URLEncoder.encode(authorQuery, StandardCharsets.UTF_8);
        String url = buildUrl("q", encodedQuery, Math.min(maxResults, 20));
        
        logger.info("Searching articles by author: " + authorName);
        return executeRequest(url);
    }
    
    @Override
    public ScholarSearchResponse searchCitingArticles(String citesId, int maxResults) throws IOException {
        if (citesId == null || citesId.trim().isEmpty()) {
            throw new IllegalArgumentException("Citation ID cannot be null or empty");
        }
        
        String url = buildCitationUrl(citesId, Math.min(maxResults, 20));
        
        logger.info("Searching citing articles for ID: " + citesId);
        return executeRequest(url);
    }
    
    @Override
    public ScholarSearchResponse searchWithPagination(String query, int startIndex, int pageSize) throws IOException {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be null or empty");
        }
        
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = buildPaginatedUrl(encodedQuery, startIndex, Math.min(pageSize, 20));
        
        logger.info("Paginated search - Query: " + query + ", Start: " + startIndex + ", Size: " + pageSize);
        return executeRequest(url);
    }
    
    @Override
    public boolean isConfigured() {
        try {
            String apiKey = configurationService.getApiKey();
            return apiKey != null && !apiKey.trim().isEmpty();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Configuration check failed", e);
            return false;
        }
    }
    
    @Override
    public String getServiceName() {
        return "SerpAPI Google Scholar Service";
    }
    
    @Override
    public boolean testConnection() {
        try {
            // Perform a minimal test search
            ScholarSearchResponse response = searchArticles("test", 1);
            return response != null && response.getSearchMetadata() != null &&
                   "Success".equals(response.getSearchMetadata().getStatus());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Connection test failed", e);
            return false;
        }
    }
    
    /**
     * Builds standard search URL with query parameter.
     */
    private String buildUrl(String paramName, String paramValue, int numResults) {
        String apiKey = configurationService.getApiKey();
        return String.format("%s?engine=%s&%s=%s&num=%d&api_key=%s",
                BASE_URL, ENGINE, paramName, paramValue, numResults, apiKey);
    }
    
    /**
     * Builds URL for citation searches.
     */
    private String buildCitationUrl(String citesId, int numResults) {
        String apiKey = configurationService.getApiKey();
        return String.format("%s?engine=%s&cites=%s&num=%d&api_key=%s",
                BASE_URL, ENGINE, citesId, numResults, apiKey);
    }
    
    /**
     * Builds URL with pagination parameters.
     */
    private String buildPaginatedUrl(String encodedQuery, int startIndex, int pageSize) {
        String apiKey = configurationService.getApiKey();
        return String.format("%s?engine=%s&q=%s&start=%d&num=%d&api_key=%s",
                BASE_URL, ENGINE, encodedQuery, startIndex, pageSize, apiKey);
    }
    
    /**
     * Executes HTTP request and parses response to ScholarSearchResponse.
     */
    private ScholarSearchResponse executeRequest(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new IOException("API request failed with status: " + 
                                    response.getStatusLine().getStatusCode() + 
                                    ", Body: " + responseBody);
            }
            
            return parseJsonResponse(responseBody);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Request execution failed for URL: " + url, e);
            throw new IOException("Failed to execute API request", e);
        }
    }
    
    /**
     * Parses JSON response string to ScholarSearchResponse object.
     */
    private ScholarSearchResponse parseJsonResponse(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            ScholarSearchResponse response = new ScholarSearchResponse();
            
            // Parse search metadata
            if (json.has("search_metadata")) {
                JSONObject metadata = json.getJSONObject("search_metadata");
                ScholarSearchResponse.SearchMetadata meta = new ScholarSearchResponse.SearchMetadata();
                if (metadata.has("id")) meta.setId(metadata.getString("id"));
                if (metadata.has("status")) meta.setStatus(metadata.getString("status"));
                response.setSearchMetadata(meta);
            }
            
            // Parse organic results (main data)
            if (json.has("organic_results")) {
                JSONArray organicArray = json.getJSONArray("organic_results");
                ScholarSearchResponse.OrganicResult[] results = new ScholarSearchResponse.OrganicResult[organicArray.length()];
                
                for (int i = 0; i < organicArray.length(); i++) {
                    JSONObject resultObj = organicArray.getJSONObject(i);
                    ScholarSearchResponse.OrganicResult result = new ScholarSearchResponse.OrganicResult();
                    
                    if (resultObj.has("position")) result.setPosition(resultObj.getInt("position"));
                    if (resultObj.has("title")) result.setTitle(resultObj.getString("title"));
                    if (resultObj.has("result_id")) result.setResultId(resultObj.getString("result_id"));
                    if (resultObj.has("link")) result.setLink(resultObj.getString("link"));
                    if (resultObj.has("snippet")) result.setSnippet(resultObj.getString("snippet"));
                    
                    // Parse publication info
                    if (resultObj.has("publication_info")) {
                        JSONObject pubInfo = resultObj.getJSONObject("publication_info");
                        ScholarSearchResponse.PublicationInfo publicationInfo = new ScholarSearchResponse.PublicationInfo();
                        if (pubInfo.has("summary")) publicationInfo.setSummary(pubInfo.getString("summary"));
                        result.setPublicationInfo(publicationInfo);
                    }
                    
                    // Parse inline links for citations
                    if (resultObj.has("inline_links")) {
                        JSONObject inlineLinksObj = resultObj.getJSONObject("inline_links");
                        ScholarSearchResponse.InlineLinks inlineLinks = new ScholarSearchResponse.InlineLinks();
                        
                        if (inlineLinksObj.has("cited_by")) {
                            JSONObject citedByObj = inlineLinksObj.getJSONObject("cited_by");
                            ScholarSearchResponse.CitedBy citedBy = new ScholarSearchResponse.CitedBy();
                            if (citedByObj.has("total")) citedBy.setTotal(citedByObj.getInt("total"));
                            if (citedByObj.has("cites_id")) citedBy.setCitesId(citedByObj.getString("cites_id"));
                            inlineLinks.setCitedBy(citedBy);
                        }
                        
                        result.setInlineLinks(inlineLinks);
                    }
                    
                    // Parse resources for PDF links
                    if (resultObj.has("resources")) {
                        JSONArray resourcesArray = resultObj.getJSONArray("resources");
                        ScholarSearchResponse.Resource[] resources = new ScholarSearchResponse.Resource[resourcesArray.length()];
                        
                        for (int j = 0; j < resourcesArray.length(); j++) {
                            JSONObject resourceObj = resourcesArray.getJSONObject(j);
                            ScholarSearchResponse.Resource resource = new ScholarSearchResponse.Resource();
                            if (resourceObj.has("title")) resource.setTitle(resourceObj.getString("title"));
                            if (resourceObj.has("file_format")) resource.setFileFormat(resourceObj.getString("file_format"));
                            if (resourceObj.has("link")) resource.setLink(resourceObj.getString("link"));
                            resources[j] = resource;
                        }
                        
                        result.setResources(resources);
                    }
                    
                    results[i] = result;
                }
                
                response.setOrganicResults(results);
            }
            
            return response;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to parse JSON response", e);
            throw new RuntimeException("JSON parsing failed", e);
        }
    }
}