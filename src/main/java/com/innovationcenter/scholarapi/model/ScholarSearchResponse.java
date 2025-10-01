package com.innovationcenter.scholarapi.model;

/**
 * Model representing the complete SerpAPI Google Scholar search response.
 * Maps directly to the JSON structure described in the Technical Report.
 */
public class ScholarSearchResponse {
    private SearchMetadata searchMetadata;
    private SearchParameters searchParameters;
    private SearchInformation searchInformation;
    private OrganicResult[] organicResults;
    private RelatedSearch[] relatedSearches;
    private Pagination pagination;
    
    // Default constructor
    public ScholarSearchResponse() {}
    
    // Getters and Setters
    public SearchMetadata getSearchMetadata() {
        return searchMetadata;
    }
    
    public void setSearchMetadata(SearchMetadata searchMetadata) {
        this.searchMetadata = searchMetadata;
    }
    
    public SearchParameters getSearchParameters() {
        return searchParameters;
    }
    
    public void setSearchParameters(SearchParameters searchParameters) {
        this.searchParameters = searchParameters;
    }
    
    public SearchInformation getSearchInformation() {
        return searchInformation;
    }
    
    public void setSearchInformation(SearchInformation searchInformation) {
        this.searchInformation = searchInformation;
    }
    
    public OrganicResult[] getOrganicResults() {
        return organicResults;
    }
    
    public void setOrganicResults(OrganicResult[] organicResults) {
        this.organicResults = organicResults;
    }
    
    public RelatedSearch[] getRelatedSearches() {
        return relatedSearches;
    }
    
    public void setRelatedSearches(RelatedSearch[] relatedSearches) {
        this.relatedSearches = relatedSearches;
    }
    
    public Pagination getPagination() {
        return pagination;
    }
    
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
    
    // Inner classes for nested JSON structure
    
    public static class SearchMetadata {
        private String id;
        private String status;
        private String createdAt;
        private String processedAt;
        private double totalTimeTaken;
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        
        public String getProcessedAt() { return processedAt; }
        public void setProcessedAt(String processedAt) { this.processedAt = processedAt; }
        
        public double getTotalTimeTaken() { return totalTimeTaken; }
        public void setTotalTimeTaken(double totalTimeTaken) { this.totalTimeTaken = totalTimeTaken; }
    }
    
    public static class SearchParameters {
        private String engine;
        private String q;
        private String apiKey;
        
        // Getters and Setters
        public String getEngine() { return engine; }
        public void setEngine(String engine) { this.engine = engine; }
        
        public String getQ() { return q; }
        public void setQ(String q) { this.q = q; }
        
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    }
    
    public static class SearchInformation {
        private long totalResults;
        private double timeTakenDisplayed;
        private String queryDisplayed;
        
        // Getters and Setters
        public long getTotalResults() { return totalResults; }
        public void setTotalResults(long totalResults) { this.totalResults = totalResults; }
        
        public double getTimeTakenDisplayed() { return timeTakenDisplayed; }
        public void setTimeTakenDisplayed(double timeTakenDisplayed) { this.timeTakenDisplayed = timeTakenDisplayed; }
        
        public String getQueryDisplayed() { return queryDisplayed; }
        public void setQueryDisplayed(String queryDisplayed) { this.queryDisplayed = queryDisplayed; }
    }
    
    public static class OrganicResult {
        private int position;
        private String title;
        private String resultId;
        private String link;
        private String snippet;
        private PublicationInfo publicationInfo;
        private InlineLinks inlineLinks;
        private Resource[] resources;
        
        // Getters and Setters
        public int getPosition() { return position; }
        public void setPosition(int position) { this.position = position; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getResultId() { return resultId; }
        public void setResultId(String resultId) { this.resultId = resultId; }
        
        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }
        
        public String getSnippet() { return snippet; }
        public void setSnippet(String snippet) { this.snippet = snippet; }
        
        public PublicationInfo getPublicationInfo() { return publicationInfo; }
        public void setPublicationInfo(PublicationInfo publicationInfo) { this.publicationInfo = publicationInfo; }
        
        public InlineLinks getInlineLinks() { return inlineLinks; }
        public void setInlineLinks(InlineLinks inlineLinks) { this.inlineLinks = inlineLinks; }
        
        public Resource[] getResources() { return resources; }
        public void setResources(Resource[] resources) { this.resources = resources; }
    }
    
    public static class PublicationInfo {
        private String summary;
        
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
    }
    
    public static class InlineLinks {
        private CitedBy citedBy;
        private Versions versions;
        
        public CitedBy getCitedBy() { return citedBy; }
        public void setCitedBy(CitedBy citedBy) { this.citedBy = citedBy; }
        
        public Versions getVersions() { return versions; }
        public void setVersions(Versions versions) { this.versions = versions; }
    }
    
    public static class CitedBy {
        private int total;
        private String link;
        private String citesId;
        
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        
        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }
        
        public String getCitesId() { return citesId; }
        public void setCitesId(String citesId) { this.citesId = citesId; }
    }
    
    public static class Versions {
        private int total;
        private String clusterId;
        
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        
        public String getClusterId() { return clusterId; }
        public void setClusterId(String clusterId) { this.clusterId = clusterId; }
    }
    
    public static class Resource {
        private String title;
        private String fileFormat;
        private String link;
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getFileFormat() { return fileFormat; }
        public void setFileFormat(String fileFormat) { this.fileFormat = fileFormat; }
        
        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }
    }
    
    public static class RelatedSearch {
        private String query;
        private String link;
        
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        
        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }
    }
    
    public static class Pagination {
        private int current;
        private String next;
        private java.util.Map<String, String> otherPages;
        
        public int getCurrent() { return current; }
        public void setCurrent(int current) { this.current = current; }
        
        public String getNext() { return next; }
        public void setNext(String next) { this.next = next; }
        
        public java.util.Map<String, String> getOtherPages() { return otherPages; }
        public void setOtherPages(java.util.Map<String, String> otherPages) { this.otherPages = otherPages; }
    }
}