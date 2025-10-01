package com.innovationcenter.scholarapi.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Model class representing a Google Scholar Author.
 * Contains all relevant information about a researcher from Google Scholar.
 */
public class Author {
    private String authorId;
    private String name;
    private String affiliation;
    private String email;
    private String interests;
    private int citedBy;
    private int hIndex;
    private int i10Index;
    private String thumbnail;
    private List<Publication> publications;

    // Default constructor
    public Author() {
        this.publications = new ArrayList<>();
    }

    // Constructor with basic information
    public Author(String authorId, String name, String affiliation) {
        this();
        this.authorId = authorId;
        this.name = name;
        this.affiliation = affiliation;
    }

    // Getters and Setters
    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public int getCitedBy() {
        return citedBy;
    }

    public void setCitedBy(int citedBy) {
        this.citedBy = citedBy;
    }

    public int getHIndex() {
        return hIndex;
    }

    public void setHIndex(int hIndex) {
        this.hIndex = hIndex;
    }

    public int getI10Index() {
        return i10Index;
    }

    public void setI10Index(int i10Index) {
        this.i10Index = i10Index;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public List<Publication> getPublications() {
        return publications;
    }

    public void setPublications(List<Publication> publications) {
        this.publications = publications != null ? publications : new ArrayList<>();
    }

    public void addPublication(Publication publication) {
        if (publication != null) {
            this.publications.add(publication);
        }
    }

    // Utility methods
    public boolean hasValidData() {
        return name != null && !name.trim().isEmpty() && authorId != null && !authorId.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Author{" +
                "authorId='" + authorId + '\'' +
                ", name='" + name + '\'' +
                ", affiliation='" + affiliation + '\'' +
                ", email='" + email + '\'' +
                ", interests='" + interests + '\'' +
                ", citedBy=" + citedBy +
                ", hIndex=" + hIndex +
                ", i10Index=" + i10Index +
                ", publicationsCount=" + (publications != null ? publications.size() : 0) +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Author author = (Author) obj;
        return authorId != null ? authorId.equals(author.authorId) : author.authorId == null;
    }

    @Override
    public int hashCode() {
        return authorId != null ? authorId.hashCode() : 0;
    }
}