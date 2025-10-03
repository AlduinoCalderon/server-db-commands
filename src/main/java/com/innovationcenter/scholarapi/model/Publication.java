package com.innovationcenter.scholarapi.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Model class representing a Publication from Google Scholar.
 * Contains information about a research publication.
 */
public class Publication {
    private String title;
    private String link;
    private String publicationId;
    private String authors;
    private String venue;
    private int year;
    private int citedBy;
    private String snippet;
    private List<String> coAuthors;

    // Default constructor
    public Publication() {
        this.coAuthors = new ArrayList<>();
    }

    // Constructor with basic information
    public Publication(String title, String authors, int year) {
        this();
        this.title = title;
        this.authors = authors;
        this.year = year;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getCitedBy() {
        return citedBy;
    }

    public void setCitedBy(int citedBy) {
        this.citedBy = citedBy;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public List<String> getCoAuthors() {
        return coAuthors;
    }

    public void setCoAuthors(List<String> coAuthors) {
        this.coAuthors = coAuthors != null ? coAuthors : new ArrayList<>();
    }

    public void addCoAuthor(String coAuthor) {
        if (coAuthor != null && !coAuthor.trim().isEmpty()) {
            this.coAuthors.add(coAuthor.trim());
        }
    }

    // Utility methods
    public boolean hasValidData() {
        return title != null && !title.trim().isEmpty() && authors != null && !authors.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Publication{" +
                "title='" + title + '\'' +
                ", authors='" + authors + '\'' +
                ", venue='" + venue + '\'' +
                ", year=" + year +
                ", citedBy=" + citedBy +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Publication publication = (Publication) obj;
        return title != null ? title.equals(publication.title) : publication.title == null;
    }

    @Override
    public int hashCode() {
        return title != null ? title.hashCode() : 0;
    }
}