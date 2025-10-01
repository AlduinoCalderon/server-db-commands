package com.innovationcenter.scholarapi.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Model class representing search results from Google Scholar API.
 * Contains a list of authors and metadata about the search.
 */
public class SearchResult {
    private List<Author> authors;
    private String searchQuery;
    private int totalResults;
    private int currentPage;
    private boolean hasNextPage;
    private String searchTime;

    // Default constructor
    public SearchResult() {
        this.authors = new ArrayList<>();
        this.currentPage = 1;
        this.hasNextPage = false;
    }

    // Constructor with search query
    public SearchResult(String searchQuery) {
        this();
        this.searchQuery = searchQuery;
    }

    // Getters and Setters
    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors != null ? authors : new ArrayList<>();
    }

    public void addAuthor(Author author) {
        if (author != null && author.hasValidData()) {
            this.authors.add(author);
        }
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public boolean hasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public String getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(String searchTime) {
        this.searchTime = searchTime;
    }

    // Utility methods
    public boolean hasResults() {
        return authors != null && !authors.isEmpty();
    }

    public int getResultCount() {
        return authors != null ? authors.size() : 0;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "searchQuery='" + searchQuery + '\'' +
                ", totalResults=" + totalResults +
                ", currentPage=" + currentPage +
                ", resultCount=" + getResultCount() +
                ", hasNextPage=" + hasNextPage +
                '}';
    }
}