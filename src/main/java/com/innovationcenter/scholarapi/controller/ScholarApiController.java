package com.innovationcenter.scholarapi.controller;

import com.innovationcenter.scholarapi.model.Author;
import com.innovationcenter.scholarapi.model.Publication;
import com.innovationcenter.scholarapi.model.SearchResult;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
 * Controller class for handling Google Scholar API requests using Apache HttpClient.
 * Implements MVC pattern for API communication and data processing.
 */
public class ScholarApiController {
    private static final Logger logger = LoggerFactory.getLogger(ScholarApiController.class);
    
    // API Configuration - Load from .env file
    private static final String BASE_URL = "https://serpapi.com/search.json";
    private static final String API_KEY_PLACEHOLDER;
    
    // Static block to load .env file
    static {
        Logger initLogger = LoggerFactory.getLogger(ScholarApiController.class);
        Dotenv dotenv = null;
        try {
            dotenv = Dotenv.configure().ignoreIfMissing().load();
            initLogger.info("Dotenv loaded successfully");
        } catch (Exception e) {
            initLogger.warn("Failed to load .env file: {}", e.getMessage());
        }
        
        String apiKey = null;
        if (dotenv != null) {
            apiKey = dotenv.get("SERPAPI_KEY");
            initLogger.info("API key from .env file: {}", apiKey != null ? "***" + apiKey.substring(Math.max(0, apiKey.length() - 4)) : "null");
        }
        if (apiKey == null || apiKey.trim().isEmpty()) {
            apiKey = System.getenv("SERPAPI_KEY");
            initLogger.info("API key from system env: {}", apiKey != null ? "***" + apiKey.substring(Math.max(0, apiKey.length() - 4)) : "null");
        }
        
        API_KEY_PLACEHOLDER = apiKey != null ? apiKey : "YOUR_SERPAPI_KEY";
        initLogger.info("Final API_KEY_PLACEHOLDER: {}", API_KEY_PLACEHOLDER.equals("YOUR_SERPAPI_KEY") ? "PLACEHOLDER" : "CONFIGURED");
    }
    private static final int DEFAULT_NUM_RESULTS = 10;
    private static final int REQUEST_TIMEOUT = 10000; // 10 seconds
    
    private final HttpClient httpClient;
    private final String apiKey;

    /**
     * Constructor with API key.
     * @param apiKey The SerpAPI key for Google Scholar access
     */
    public ScholarApiController(String apiKey) {
        this.httpClient = HttpClients.createDefault();
        this.apiKey = apiKey != null ? apiKey : API_KEY_PLACEHOLDER;
        
        if (!isApiKeyConfigured()) {
            logger.warn("Using placeholder API key. Please set a valid SerpAPI key.");
        } else {
            logger.info("API key configured successfully from environment.");
        }
    }

    /**
     * Default constructor - uses placeholder API key.
     */
    public ScholarApiController() {
        this(null);
    }

    /**
     * Search for authors using Google Scholar API.
     * @param query The search query (author name)
     * @param maxResults Maximum number of results to return
     * @return SearchResult containing found authors
     */
    public SearchResult searchAuthors(String query, int maxResults) {
        if (query == null || query.trim().isEmpty()) {
            logger.error("Search query cannot be null or empty");
            return createEmptySearchResult(query, "Invalid search query");
        }

        try {
            String url = buildSearchUrl(query, maxResults);
            logger.info("Making API request to: {}", url.replace(apiKey, "***"));

            String jsonResponse = makeHttpRequest(url);
            if (jsonResponse == null) {
                return createEmptySearchResult(query, "Failed to get response from API");
            }

            return parseSearchResponse(jsonResponse, query);

        } catch (Exception e) {
            logger.error("Error searching for authors: {}", e.getMessage(), e);
            return createEmptySearchResult(query, "Error occurred during search: " + e.getMessage());
        }
    }

    /**
     * Search for authors with default result limit.
     * @param query The search query (author name)
     * @return SearchResult containing found authors
     */
    public SearchResult searchAuthors(String query) {
        return searchAuthors(query, DEFAULT_NUM_RESULTS);
    }

    /**
     * Get detailed information about a specific author.
     * @param authorId The Google Scholar author ID
     * @return Author object with detailed information
     */
    public Author getAuthorDetails(String authorId) {
        if (authorId == null || authorId.trim().isEmpty()) {
            logger.error("Author ID cannot be null or empty");
            return null;
        }

        try {
            String url = buildAuthorUrl(authorId);
            logger.info("Getting author details for ID: {}", authorId);

            String jsonResponse = makeHttpRequest(url);
            if (jsonResponse == null) {
                return null;
            }

            return parseAuthorResponse(jsonResponse);

        } catch (Exception e) {
            logger.error("Error getting author details for ID {}: {}", authorId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Build the search URL for author search according to technical documentation.
     * @param query The search query
     * @param maxResults Maximum results to return
     * @return The complete URL string
     */
    private String buildSearchUrl(String query, int maxResults) {
        try {
            // Use author: prefix for author searches as per documentation
            String searchQuery = "author:\"" + query + "\"";
            String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8.toString());
            return String.format("%s?engine=google_scholar&q=%s&api_key=%s&num=%d",
                    BASE_URL, encodedQuery, apiKey, maxResults);
        } catch (Exception e) {
            logger.error("Error encoding search query: {}", e.getMessage());
            throw new RuntimeException("Failed to build search URL", e);
        }
    }

    /**
     * Build the URL for getting specific author details.
     * @param authorId The author ID
     * @return The complete URL string
     */
    private String buildAuthorUrl(String authorId) {
        return String.format("%s?engine=google_scholar_author&author_id=%s&api_key=%s",
                BASE_URL, authorId, apiKey);
    }

    /**
     * Make HTTP GET request to the specified URL.
     * @param url The URL to request
     * @return The response body as string, or null on error
     */
    private String makeHttpRequest(String url) {
        HttpGet httpGet = new HttpGet(url);
        
        // Set headers
        httpGet.setHeader("User-Agent", "Scholar-API-Client/1.0");
        httpGet.setHeader("Accept", "application/json");

        try {
            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseBody = EntityUtils.toString(entity);
                    logger.debug("API response received, length: {}", responseBody.length());
                    return responseBody;
                }
            } else {
                logger.error("HTTP request failed with status code: {}", statusCode);
                
                // Try to get error message from response
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String errorResponse = EntityUtils.toString(entity);
                    logger.error("Error response: {}", errorResponse);
                }
            }

        } catch (IOException e) {
            logger.error("IOException during HTTP request: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during HTTP request: {}", e.getMessage());
        }

        return null;
    }

    /**
     * Parse the JSON response from author search API.
     * @param jsonResponse The JSON response string
     * @param originalQuery The original search query
     * @return SearchResult with parsed authors
     */
    private SearchResult parseSearchResponse(String jsonResponse, String originalQuery) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            SearchResult searchResult = new SearchResult(originalQuery);

            // Check for API errors
            if (jsonObject.has("error")) {
                logger.error("API returned error: {}", jsonObject.getString("error"));
                return searchResult;
            }

            // Parse organic_results array (regular Google Scholar search)
            if (jsonObject.has("organic_results")) {
                JSONArray results = jsonObject.getJSONArray("organic_results");
                
                for (int i = 0; i < results.length(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    Author author = parseAuthorFromSearchResult(result);
                    
                    if (author != null && author.hasValidData()) {
                        searchResult.addAuthor(author);
                    }
                }
            }

            // Parse pagination info
            if (jsonObject.has("pagination")) {
                JSONObject pagination = jsonObject.getJSONObject("pagination");
                if (pagination.has("next")) {
                    searchResult.setHasNextPage(true);
                }
            }

            logger.info("Parsed {} authors from search response", searchResult.getResultCount());
            return searchResult;

        } catch (Exception e) {
            logger.error("Error parsing search response: {}", e.getMessage(), e);
            return createEmptySearchResult(originalQuery, "Error parsing API response");
        }
    }

    /**
     * Parse an author object from a search result JSON according to documentation.
     * @param result The JSON object representing a search result from organic_results
     * @return Author object, or null if parsing fails
     */
    private Author parseAuthorFromSearchResult(JSONObject result) {
        try {
            Author author = new Author();

            // Extract author information from publication_info.summary as per documentation
            if (result.has("publication_info") && result.getJSONObject("publication_info").has("summary")) {
                String summary = result.getJSONObject("publication_info").getString("summary");
                // Parse format: "Author1, Author2 - Source, Year - Publisher"
                String[] parts = summary.split(" - ");
                if (parts.length > 0) {
                    author.setName(parts[0].trim()); // First part is authors
                }
            }
            
            if (result.has("result_id")) {
                author.setAuthorId(result.getString("result_id"));
            }
            
            // Extract citations from inline_links.cited_by.total
            if (result.has("inline_links") && result.getJSONObject("inline_links").has("cited_by")) {
                JSONObject citedBy = result.getJSONObject("inline_links").getJSONObject("cited_by");
                if (citedBy.has("total")) {
                    author.setCitedBy(citedBy.getInt("total"));
                }
            }
            
            if (result.has("snippet")) {
                String snippet = result.getString("snippet");
                // Try to extract affiliation from snippet
                String affiliation = extractAffiliationFromSnippet(snippet);
                if (affiliation != null) {
                    author.setAffiliation(affiliation);
                }
            }
            
            // Extract publication info and add to author
            Publication publication = parsePublicationFromSearchResult(result);
            if (publication != null) {
                author.addPublication(publication);
            }

            return author;

        } catch (Exception e) {
            logger.error("Error parsing author from search result: {}", e.getMessage());
            return null;
        }
    }
    

    
    /**
     * Extract affiliation from snippet text.
     * @param snippet The snippet text
     * @return Extracted affiliation or null
     */
    private String extractAffiliationFromSnippet(String snippet) {
        if (snippet == null || snippet.isEmpty()) {
            return null;
        }
        
        // Look for common university/institution keywords
        String[] keywords = {"University", "Institute", "College", "Research", "Department"};
        for (String keyword : keywords) {
            if (snippet.toLowerCase().contains(keyword.toLowerCase())) {
                // Extract sentence containing the keyword
                String[] sentences = snippet.split("\\.");
                for (String sentence : sentences) {
                    if (sentence.toLowerCase().contains(keyword.toLowerCase())) {
                        return sentence.trim();
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Parse a publication from a search result JSON object per documentation.
     * @param result The JSON object representing a search result
     * @return Publication object, or null if parsing fails
     */
    private Publication parsePublicationFromSearchResult(JSONObject result) {
        try {
            Publication publication = new Publication();

            if (result.has("title")) {
                publication.setTitle(result.getString("title"));
            }
            
            if (result.has("link")) {
                publication.setLink(result.getString("link"));
            }
            
            if (result.has("result_id")) {
                publication.setPublicationId(result.getString("result_id"));
            }
            
            if (result.has("snippet")) {
                publication.setSnippet(result.getString("snippet"));
            }
            
            // Parse publication_info.summary as per documentation
            // Format: "Author1, Author2 - Source, Year - Publisher"
            if (result.has("publication_info") && result.getJSONObject("publication_info").has("summary")) {
                String summary = result.getJSONObject("publication_info").getString("summary");
                String[] parts = summary.split(" - ");
                
                if (parts.length >= 2) {
                    publication.setAuthors(parts[0].trim());
                    
                    // Extract year and source from "Source, Year" format
                    if (parts[1].contains(",")) {
                        String[] sourceYear = parts[1].split(",");
                        if (sourceYear.length >= 2) {
                            publication.setVenue(sourceYear[0].trim());
                            try {
                                publication.setYear(Integer.parseInt(sourceYear[1].trim()));
                            } catch (NumberFormatException e) {
                                logger.debug("Could not parse year from: {}", sourceYear[1]);
                            }
                        }
                    }
                }
            }
            
            // Extract citation count from inline_links.cited_by.total
            if (result.has("inline_links") && result.getJSONObject("inline_links").has("cited_by")) {
                JSONObject citedBy = result.getJSONObject("inline_links").getJSONObject("cited_by");
                if (citedBy.has("total")) {
                    publication.setCitedBy(citedBy.getInt("total"));
                }
            }

            return publication;

        } catch (Exception e) {
            logger.error("Error parsing publication from search result: {}", e.getMessage());
            return null;
        }
    }
    


    /**
     * Parse the JSON response from author details API.
     * @param jsonResponse The JSON response string
     * @return Author object with detailed information
     */
    private Author parseAuthorResponse(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // Check for API errors
            if (jsonObject.has("error")) {
                logger.error("API returned error: {}", jsonObject.getString("error"));
                return null;
            }

            Author author = new Author();

            // Parse author information
            if (jsonObject.has("author")) {
                JSONObject authorObj = jsonObject.getJSONObject("author");
                
                if (authorObj.has("author_id")) {
                    author.setAuthorId(authorObj.getString("author_id"));
                }
                if (authorObj.has("name")) {
                    author.setName(authorObj.getString("name"));
                }
                if (authorObj.has("affiliation")) {
                    author.setAffiliation(authorObj.getString("affiliation"));
                }
                if (authorObj.has("email")) {
                    author.setEmail(authorObj.getString("email"));
                }
                if (authorObj.has("thumbnail")) {
                    author.setThumbnail(authorObj.getString("thumbnail"));
                }

                // Parse interests
                if (authorObj.has("interests")) {
                    JSONArray interests = authorObj.getJSONArray("interests");
                    List<String> interestList = new ArrayList<>();
                    for (int i = 0; i < interests.length(); i++) {
                        JSONObject interest = interests.getJSONObject(i);
                        if (interest.has("title")) {
                            interestList.add(interest.getString("title"));
                        }
                    }
                    author.setInterests(String.join(", ", interestList));
                }
            }

            // Parse citation metrics
            if (jsonObject.has("cited_by")) {
                JSONObject citedBy = jsonObject.getJSONObject("cited_by");
                if (citedBy.has("table")) {
                    JSONArray table = citedBy.getJSONArray("table");
                    if (table.length() > 0) {
                        JSONObject metrics = table.getJSONObject(0);
                        if (metrics.has("citations")) {
                            JSONObject citations = metrics.getJSONObject("citations");
                            if (citations.has("all")) {
                                author.setCitedBy(citations.getInt("all"));
                            }
                        }
                        if (metrics.has("h_index")) {
                            JSONObject hIndex = metrics.getJSONObject("h_index");
                            if (hIndex.has("all")) {
                                author.setHIndex(hIndex.getInt("all"));
                            }
                        }
                        if (metrics.has("i10_index")) {
                            JSONObject i10Index = metrics.getJSONObject("i10_index");
                            if (i10Index.has("all")) {
                                author.setI10Index(i10Index.getInt("all"));
                            }
                        }
                    }
                }
            }

            // Parse articles/publications
            if (jsonObject.has("articles")) {
                JSONArray articles = jsonObject.getJSONArray("articles");
                List<Publication> publications = new ArrayList<>();
                
                for (int i = 0; i < articles.length(); i++) {
                    JSONObject article = articles.getJSONObject(i);
                    Publication publication = parsePublicationFromArticle(article);
                    
                    if (publication != null && publication.hasValidData()) {
                        publications.add(publication);
                    }
                }
                
                author.setPublications(publications);
            }

            return author;

        } catch (Exception e) {
            logger.error("Error parsing author response: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Parse a publication from an article JSON object.
     * @param article The JSON object representing an article
     * @return Publication object, or null if parsing fails
     */
    private Publication parsePublicationFromArticle(JSONObject article) {
        try {
            Publication publication = new Publication();

            if (article.has("title")) {
                publication.setTitle(article.getString("title"));
            }
            
            if (article.has("link")) {
                publication.setLink(article.getString("link"));
            }
            
            if (article.has("publication_id")) {
                publication.setPublicationId(article.getString("publication_id"));
            }
            
            if (article.has("authors")) {
                publication.setAuthors(article.getString("authors"));
            }
            
            if (article.has("publication")) {
                publication.setVenue(article.getString("publication"));
            }
            
            if (article.has("year")) {
                try {
                    publication.setYear(article.getInt("year"));
                } catch (Exception e) {
                    // Year might be a string, try to parse it
                    String yearStr = article.getString("year");
                    publication.setYear(Integer.parseInt(yearStr));
                }
            }
            
            if (article.has("cited_by")) {
                JSONObject citedBy = article.getJSONObject("cited_by");
                if (citedBy.has("value")) {
                    publication.setCitedBy(citedBy.getInt("value"));
                }
            }

            return publication;

        } catch (Exception e) {
            logger.error("Error parsing publication from article: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Create an empty search result with error information.
     * @param query The original search query
     * @param errorMessage The error message
     * @return Empty SearchResult
     */
    private SearchResult createEmptySearchResult(String query, String errorMessage) {
        SearchResult result = new SearchResult(query);
        logger.warn("Creating empty search result: {}", errorMessage);
        return result;
    }

    /**
     * Check if the API key is properly configured.
     * @return true if API key appears to be valid
     */
    public boolean isApiKeyConfigured() {
        return apiKey != null && !apiKey.trim().isEmpty() && 
               !"YOUR_SERPAPI_KEY".equals(apiKey) && !apiKey.startsWith("YOUR_");
    }

    /**
     * Get current API key status for debugging.
     * @return Status message about API key
     */
    public String getApiKeyStatus() {
        if (!isApiKeyConfigured()) {
            return "API key not configured. Please set a valid SerpAPI key.";
        }
        return "API key configured successfully.";
    }
}