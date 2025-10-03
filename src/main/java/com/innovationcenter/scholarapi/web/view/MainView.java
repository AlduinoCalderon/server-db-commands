package com.innovationcenter.scholarapi.web.view;

import com.innovationcenter.scholarapi.model.Article;
import com.innovationcenter.scholarapi.repository.ArticleRepository;
import com.innovationcenter.scholarapi.service.ArticleIntegrationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Set;

@Route("")
public class MainView extends VerticalLayout {

    private final ArticleRepository articleRepository;
    private final ArticleIntegrationService integrationService;
    
    private Grid<Article> articleGrid;

    public MainView(ArticleRepository articleRepository, 
                    ArticleIntegrationService integrationService) {
        this.articleRepository = articleRepository;
        this.integrationService = integrationService;
        
        setSizeFull();
        setPadding(true);
        
        H1 header = new H1("Scholar API - Research Article Management");
        header.getStyle().set("color", "#2c5f7c");
        
        Tab searchTab = new Tab("Search & Import");
        Tab browseTab = new Tab("Browse Articles");
        Tab statsTab = new Tab("Statistics");
        
        Tabs tabs = new Tabs(searchTab, browseTab, statsTab);
        tabs.setWidthFull();
        
        VerticalLayout contentArea = new VerticalLayout();
        contentArea.setSizeFull();
        contentArea.setPadding(true);
        
        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();
            contentArea.removeAll();
            String label = selectedTab.getLabel();
            
            if ("Search & Import".equals(label)) {
                contentArea.add(createSearchView());
            } else if ("Browse Articles".equals(label)) {
                contentArea.add(createBrowseView());
            } else if ("Statistics".equals(label)) {
                contentArea.add(createStatsView());
            }
        });
        
        add(header, tabs, contentArea);
        contentArea.add(createSearchView());
    }
    
    private VerticalLayout createSearchView() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        
        H3 heading = new H3("Search Google Scholar and Import Articles");
        Paragraph hint = new Paragraph("Search for articles and automatically save them to the database");
        hint.getStyle().set("color", "#666").set("margin-top", "0");
        
        ComboBox<String> searchType = new ComboBox<>("Search by:");
        searchType.setItems("Researcher Name", "Query/Keywords", "Title");
        searchType.setValue("Researcher Name");
        searchType.setWidth("200px");
        
        TextField searchField = new TextField("Search term");
        searchField.setPlaceholder("Enter search term...");
        searchField.setWidth("400px");
        
        IntegerField maxResultsField = new IntegerField("Max Results");
        maxResultsField.setValue(10);
        maxResultsField.setMin(1);
        maxResultsField.setMax(100);
        maxResultsField.setWidth("150px");
        
        Button searchBtn = new Button("Search & Import");
        searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        Grid<Article> searchResultsGrid = new Grid<>(Article.class, false);
        searchResultsGrid.addColumn(Article::getTitle).setHeader("Title").setFlexGrow(1);
        searchResultsGrid.addColumn(Article::getAuthors).setHeader("Authors").setWidth("250px");
        searchResultsGrid.addColumn(Article::getPublicationYear).setHeader("Year").setWidth("100px");
        searchResultsGrid.addColumn(Article::getCitationCount).setHeader("Citations").setWidth("100px");
        searchResultsGrid.setHeight("400px");
        searchResultsGrid.setVisible(false);
        
        searchBtn.addClickListener(e -> {
            String searchTerm = searchField.getValue();
            String type = searchType.getValue();
            Integer maxResults = maxResultsField.getValue();
            
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                showNotification("Please enter a search term", NotificationVariant.LUMO_ERROR);
                return;
            }
            
            try {
                searchBtn.setEnabled(false);
                showNotification("Searching " + type + " for: " + searchTerm + "...", NotificationVariant.LUMO_PRIMARY);
                
                List<Article> results = integrationService.searchAndStoreArticles(
                    searchTerm, 
                    maxResults != null ? maxResults : 10
                );
                
                searchResultsGrid.setItems(results);
                searchResultsGrid.setVisible(true);
                showNotification("Successfully imported " + results.size() + " articles!", NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                showNotification("Error: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
            } finally {
                searchBtn.setEnabled(true);
            }
        });
        
        HorizontalLayout controls = new HorizontalLayout(searchType, searchField, maxResultsField, searchBtn);
        controls.setAlignItems(Alignment.END);
        
        layout.add(heading, hint, controls, searchResultsGrid);
        return layout;
    }
    
    private VerticalLayout createBrowseView() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        
        H3 heading = new H3("Browse Database Articles");
        Paragraph hint = new Paragraph("Filter and manage articles stored in the database");
        hint.getStyle().set("color", "#666").set("margin-top", "0");
        
        ComboBox<String> filterType = new ComboBox<>("Filter by:");
        filterType.setItems("All Articles", "By Author", "By Year (and newer)", "Highly Cited (minimum)");
        filterType.setValue("All Articles");
        filterType.setWidth("200px");
        
        TextField filterValue = new TextField("Filter value");
        filterValue.setPlaceholder("Enter filter value...");
        filterValue.setWidth("300px");
        filterValue.setEnabled(false);
        
        filterType.addValueChangeListener(e -> {
            String selected = e.getValue();
            filterValue.setEnabled(!"All Articles".equals(selected));
            switch (selected) {
                case "By Author":
                    filterValue.setPlaceholder("Enter author name...");
                    break;
                case "By Year (and newer)":
                    filterValue.setPlaceholder("Enter year (e.g., 2020)...");
                    break;
                case "Highly Cited (minimum)":
                    filterValue.setPlaceholder("Enter minimum citations...");
                    break;
                default:
                    filterValue.setPlaceholder("");
            }
        });
        
        Button applyFilterBtn = new Button("Apply Filter");
        applyFilterBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        Button viewDetailsBtn = new Button("View Details");
        viewDetailsBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        viewDetailsBtn.setEnabled(false);
        
        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteBtn.setEnabled(false);
        
        HorizontalLayout filters = new HorizontalLayout(filterType, filterValue, applyFilterBtn);
        filters.setAlignItems(Alignment.END);
        
        HorizontalLayout controls = new HorizontalLayout(viewDetailsBtn, deleteBtn);
        controls.setAlignItems(Alignment.END);
        
        articleGrid = new Grid<>(Article.class, false);
        articleGrid.addColumn(Article::getId).setHeader("ID").setWidth("80px").setFlexGrow(0);
        articleGrid.addColumn(Article::getTitle).setHeader("Title").setFlexGrow(1);
        articleGrid.addColumn(Article::getAuthors).setHeader("Authors").setWidth("200px");
        articleGrid.addColumn(Article::getPublicationYear).setHeader("Year").setWidth("100px");
        articleGrid.addColumn(Article::getCitationCount).setHeader("Citations").setWidth("120px");
        articleGrid.setSizeFull();
        articleGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        
        articleGrid.addSelectionListener(event -> {
            Set<Article> selected = event.getAllSelectedItems();
            viewDetailsBtn.setEnabled(selected.size() == 1);
            deleteBtn.setEnabled(!selected.isEmpty());
        });
        
        applyFilterBtn.addClickListener(e -> {
            String type = filterType.getValue();
            String value = filterValue.getValue();
            filterArticlesByType(type, value);
        });
        
        viewDetailsBtn.addClickListener(e -> {
            Set<Article> selected = articleGrid.getSelectedItems();
            if (selected.size() == 1) {
                showArticleDetails(selected.iterator().next());
            }
        });
        
        deleteBtn.addClickListener(e -> {
            Set<Article> selectedArticles = articleGrid.getSelectedItems();
            confirmAndDeleteArticles(selectedArticles);
        });
        
        layout.add(heading, hint, filters, controls, articleGrid);
        filterArticlesByType("All Articles", null);
        return layout;
    }
    
    private void filterArticlesByType(String filterType, String filterValue) {
        try {
            List<Article> articles = articleRepository.findAll();
            
            switch (filterType) {
                case "All Articles":
                    break;
                    
                case "By Author":
                    if (filterValue != null && !filterValue.trim().isEmpty()) {
                        String lowerAuthor = filterValue.toLowerCase();
                        articles = articles.stream()
                            .filter(a -> a.getAuthors() != null && 
                                   a.getAuthors().toLowerCase().contains(lowerAuthor))
                            .collect(java.util.stream.Collectors.toList());
                    }
                    break;
                    
                case "By Year (and newer)":
                    if (filterValue != null && !filterValue.trim().isEmpty()) {
                        try {
                            int year = Integer.parseInt(filterValue.trim());
                            articles = articles.stream()
                                .filter(a -> a.getPublicationYear() != null && 
                                       a.getPublicationYear() >= year)
                                .collect(java.util.stream.Collectors.toList());
                        } catch (NumberFormatException e) {
                            showNotification("Invalid year format", NotificationVariant.LUMO_ERROR);
                            return;
                        }
                    }
                    break;
                    
                case "Highly Cited (minimum)":
                    if (filterValue != null && !filterValue.trim().isEmpty()) {
                        try {
                            int minCitations = Integer.parseInt(filterValue.trim());
                            articles = articles.stream()
                                .filter(a -> a.getCitationCount() >= minCitations)
                                .collect(java.util.stream.Collectors.toList());
                        } catch (NumberFormatException e) {
                            showNotification("Invalid number format", NotificationVariant.LUMO_ERROR);
                            return;
                        }
                    }
                    break;
            }
            
            articleGrid.setItems(articles);
            showNotification("Found " + articles.size() + " articles", NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            showNotification("Error loading articles: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            articleGrid.setItems();
        }
    }
    
    private void showArticleDetails(Article article) {
        Dialog detailsDialog = new Dialog();
        detailsDialog.setHeaderTitle("Article Details");
        detailsDialog.setWidth("700px");
        
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);
        
        Div titleSection = new Div();
        Span titleLabel = new Span("Title: ");
        titleLabel.getStyle().set("font-weight", "bold");
        Span titleValue = new Span(article.getTitle() != null ? article.getTitle() : "N/A");
        titleSection.add(titleLabel, titleValue);
        
        Div authorsSection = new Div();
        Span authorsLabel = new Span("Authors: ");
        authorsLabel.getStyle().set("font-weight", "bold");
        Span authorsValue = new Span(article.getAuthors() != null ? article.getAuthors() : "N/A");
        authorsSection.add(authorsLabel, authorsValue);
        
        Div yearSection = new Div();
        Span yearLabel = new Span("Year: ");
        yearLabel.getStyle().set("font-weight", "bold");
        Span yearValue = new Span(article.getPublicationYear() != null ? 
            article.getPublicationYear().toString() : "N/A");
        yearSection.add(yearLabel, yearValue);
        
        if (article.getJournal() != null && !article.getJournal().isEmpty()) {
            Div journalSection = new Div();
            Span journalLabel = new Span("Journal: ");
            journalLabel.getStyle().set("font-weight", "bold");
            Span journalValue = new Span(article.getJournal());
            journalSection.add(journalLabel, journalValue);
            content.add(journalSection);
        }
        
        Div citationsSection = new Div();
        Span citationsLabel = new Span("Citations: ");
        citationsLabel.getStyle().set("font-weight", "bold");
        Span citationsValue = new Span(String.valueOf(article.getCitationCount()));
        citationsSection.add(citationsLabel, citationsValue);
        
        if (article.getUrl() != null && !article.getUrl().isEmpty()) {
            Div urlSection = new Div();
            Span urlLabel = new Span("URL: ");
            urlLabel.getStyle().set("font-weight", "bold");
            Span urlValue = new Span(article.getUrl());
            urlValue.getStyle().set("word-break", "break-all");
            urlSection.add(urlLabel, urlValue);
            content.add(urlSection);
        }
        
        if (article.getSnippet() != null && !article.getSnippet().isEmpty()) {
            Div snippetSection = new Div();
            Span snippetLabel = new Span("Snippet: ");
            snippetLabel.getStyle().set("font-weight", "bold");
            Paragraph snippetValue = new Paragraph(article.getSnippet());
            snippetValue.getStyle().set("font-style", "italic");
            snippetSection.add(snippetLabel);
            content.add(snippetSection, snippetValue);
        }
        
        content.add(titleSection, authorsSection, yearSection, citationsSection);
        
        Button closeBtn = new Button("Close", e -> detailsDialog.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        detailsDialog.add(content);
        detailsDialog.getFooter().add(closeBtn);
        detailsDialog.open();
    }
    
    private void confirmAndDeleteArticles(Set<Article> articles) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Confirm Deletion");
        
        VerticalLayout content = new VerticalLayout();
        content.add(new Paragraph("Are you sure you want to delete " + articles.size() + " article(s)?"));
        content.add(new Paragraph("This action cannot be undone."));
        
        Button confirmBtn = new Button("Delete", e -> {
            deleteArticles(articles);
            confirmDialog.close();
        });
        confirmBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        
        Button cancelBtn = new Button("Cancel", e -> confirmDialog.close());
        
        HorizontalLayout buttons = new HorizontalLayout(confirmBtn, cancelBtn);
        content.add(buttons);
        
        confirmDialog.add(content);
        confirmDialog.open();
    }
    
    private void deleteArticles(Set<Article> articles) {
        int deleted = 0;
        for (Article article : articles) {
            try {
                articleRepository.deleteById(article.getId());
                deleted++;
            } catch (Exception e) {
                // Continue deleting others
            }
        }
        
        if (deleted > 0) {
            showNotification("Deleted " + deleted + " article(s)", NotificationVariant.LUMO_SUCCESS);
            filterArticlesByType("All Articles", null);
        } else {
            showNotification("No articles were deleted", NotificationVariant.LUMO_ERROR);
        }
    }
    
    private VerticalLayout createStatsView() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        
        H3 heading = new H3("Database Statistics");
        
        try {
            long articleCount = articleRepository.count();
            List<Article> articles = articleRepository.findAll();
            
            int totalCitations = articles.stream()
                .mapToInt(Article::getCitationCount)
                .sum();
            
            double avgCitations = articles.isEmpty() ? 0 : 
                articles.stream()
                    .mapToInt(Article::getCitationCount)
                    .average()
                    .orElse(0);
            
            Paragraph articlesInfo = new Paragraph("Total Articles: " + articleCount);
            articlesInfo.getStyle().set("font-size", "18px");
            
            Paragraph citationsInfo = new Paragraph("Total Citations: " + totalCitations);
            citationsInfo.getStyle().set("font-size", "18px");
            
            Paragraph avgInfo = new Paragraph(String.format("Average Citations: %.2f", avgCitations));
            avgInfo.getStyle().set("font-size", "18px");
            
            layout.add(heading, articlesInfo, citationsInfo, avgInfo);
            
        } catch (Exception e) {
            layout.add(heading, new Paragraph("Error loading statistics: " + e.getMessage()));
        }
        
        return layout;
    }
    
    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(variant);
        notification.open();
    }
}
