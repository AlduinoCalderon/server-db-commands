package com.innovationcenter.scholarapi.web.view;

import com.innovationcenter.scholarapi.model.Article;
import com.innovationcenter.scholarapi.repository.ArticleRepository;
import com.innovationcenter.scholarapi.service.ArticleIntegrationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
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
        
        H3 title = new H3("Search Google Scholar and Import Articles");
        
        TextField researcherField = new TextField("Researcher Name");
        researcherField.setPlaceholder("Enter researcher name...");
        researcherField.setWidth("400px");
        
        IntegerField maxResultsField = new IntegerField("Max Results");
        maxResultsField.setValue(10);
        maxResultsField.setMin(1);
        maxResultsField.setMax(100);
        maxResultsField.setWidth("150px");
        
        Button searchByResearcherBtn = new Button("Search by Researcher");
        searchByResearcherBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchByResearcherBtn.addClickListener(e -> {
            String researcher = researcherField.getValue();
            Integer maxResults = maxResultsField.getValue();
            if (researcher != null && !researcher.trim().isEmpty()) {
                try {
                    showNotification("Searching for articles by " + researcher + "...", NotificationVariant.LUMO_PRIMARY);
                    List<Article> imported = integrationService.searchAndStoreArticles(researcher, maxResults != null ? maxResults : 10);
                    showNotification("Successfully imported " + imported.size() + " articles!", NotificationVariant.LUMO_SUCCESS);
                } catch (Exception ex) {
                    showNotification("Error importing articles: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
                }
            } else {
                showNotification("Please enter a researcher name", NotificationVariant.LUMO_ERROR);
            }
        });
        
        HorizontalLayout researcherSearch = new HorizontalLayout(researcherField, maxResultsField, searchByResearcherBtn);
        researcherSearch.setAlignItems(Alignment.END);
        
        layout.add(title, researcherSearch);
        return layout;
    }
    
    private VerticalLayout createBrowseView() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        
        TextField titleFilter = new TextField("Title");
        titleFilter.setPlaceholder("Filter by title...");
        titleFilter.setClearButtonVisible(true);
        
        TextField authorFilter = new TextField("Author");
        authorFilter.setPlaceholder("Filter by author...");
        authorFilter.setClearButtonVisible(true);
        
        IntegerField yearFilter = new IntegerField("Year");
        yearFilter.setPlaceholder("Filter by year...");
        yearFilter.setClearButtonVisible(true);
        
        IntegerField minCitationsFilter = new IntegerField("Min Citations");
        minCitationsFilter.setPlaceholder("Min citations...");
        minCitationsFilter.setClearButtonVisible(true);
        
        Button refreshBtn = new Button("Refresh");
        refreshBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteBtn.setEnabled(false);
        
        HorizontalLayout filters = new HorizontalLayout(titleFilter, authorFilter, yearFilter, minCitationsFilter);
        HorizontalLayout controls = new HorizontalLayout(refreshBtn, deleteBtn);
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
            deleteBtn.setEnabled(!event.getAllSelectedItems().isEmpty());
        });
        
        Runnable filterFunc = () -> {
            try {
                List<Article> articles = articleRepository.findAll();
                
                String titleVal = titleFilter.getValue();
                if (titleVal != null && !titleVal.trim().isEmpty()) {
                    String lowerTitle = titleVal.toLowerCase();
                    articles = articles.stream()
                        .filter(a -> a.getTitle() != null && a.getTitle().toLowerCase().contains(lowerTitle))
                        .collect(java.util.stream.Collectors.toList());
                }
                
                String authorVal = authorFilter.getValue();
                if (authorVal != null && !authorVal.trim().isEmpty()) {
                    String lowerAuthor = authorVal.toLowerCase();
                    articles = articles.stream()
                        .filter(a -> a.getAuthors() != null && a.getAuthors().toLowerCase().contains(lowerAuthor))
                        .collect(java.util.stream.Collectors.toList());
                }
                
                Integer yearVal = yearFilter.getValue();
                if (yearVal != null) {
                    articles = articles.stream()
                        .filter(a -> a.getPublicationYear() != null && a.getPublicationYear().equals(yearVal))
                        .collect(java.util.stream.Collectors.toList());
                }
                
                Integer minCitVal = minCitationsFilter.getValue();
                if (minCitVal != null) {
                    articles = articles.stream()
                        .filter(a -> a.getCitationCount() >= minCitVal)
                        .collect(java.util.stream.Collectors.toList());
                }
                
                articleGrid.setItems(articles);
            } catch (Exception e) {
                showNotification("Error loading articles: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
                articleGrid.setItems();
            }
        };
        
        refreshBtn.addClickListener(e -> filterFunc.run());
        titleFilter.addValueChangeListener(e -> filterFunc.run());
        authorFilter.addValueChangeListener(e -> filterFunc.run());
        yearFilter.addValueChangeListener(e -> filterFunc.run());
        minCitationsFilter.addValueChangeListener(e -> filterFunc.run());
        
        deleteBtn.addClickListener(e -> {
            Set<Article> selectedArticles = articleGrid.getSelectedItems();
            Dialog confirmDialog = new Dialog();
            confirmDialog.setHeaderTitle("Confirm Deletion");
            
            VerticalLayout content = new VerticalLayout();
            content.add(new Paragraph("Are you sure you want to delete " + selectedArticles.size() + " article(s)?"));
            content.add(new Paragraph("This action cannot be undone."));
            
            Button confirmBtn = new Button("Delete", ev -> {
                int deleted = 0;
                for (Article article : selectedArticles) {
                    try {
                        articleRepository.deleteById(article.getId());
                        deleted++;
                    } catch (Exception ex) {
                        // Continue deleting others
                    }
                }
                if (deleted > 0) {
                    showNotification("Deleted " + deleted + " article(s)", NotificationVariant.LUMO_SUCCESS);
                    filterFunc.run();
                } else {
                    showNotification("No articles were deleted", NotificationVariant.LUMO_ERROR);
                }
                confirmDialog.close();
            });
            confirmBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
            
            Button cancelBtn = new Button("Cancel", ev -> confirmDialog.close());
            
            HorizontalLayout buttons = new HorizontalLayout(confirmBtn, cancelBtn);
            content.add(buttons);
            
            confirmDialog.add(content);
            confirmDialog.open();
        });
        
        layout.add(filters, controls, articleGrid);
        filterFunc.run();
        return layout;
    }
    
    
    private VerticalLayout createStatsView() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        
        H3 title = new H3("Database Statistics");
        
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
            
            layout.add(title, articlesInfo, citationsInfo, avgInfo);
            
        } catch (Exception e) {
            layout.add(title, new Paragraph("Error loading statistics: " + e.getMessage()));
        }
        
        return layout;
    }
    
    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(variant);
        notification.open();
    }
}
