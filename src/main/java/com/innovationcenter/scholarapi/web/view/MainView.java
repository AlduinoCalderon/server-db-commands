package com.innovationcenter.scholarapi.web.view;

import com.innovationcenter.scholarapi.model.Article;
import com.innovationcenter.scholarapi.model.SimpleAuthor;
import com.innovationcenter.scholarapi.repository.ArticleRepository;
import com.innovationcenter.scholarapi.repository.SimpleAuthorRepository;
import com.innovationcenter.scholarapi.service.ArticleIntegrationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main Vaadin view - Homepage with tabs similar to JavaFX version
 */
@Route("")
public class MainView extends VerticalLayout {

    private final ArticleRepository articleRepository;
    
    private Tabs tabs;
    private VerticalLayout contentArea;

    public MainView(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
        
        setSizeFull();
        setPadding(true);
        
        // Header
        H1 header = new H1("Scholar API - Research Article Management");
        header.getStyle().set("color", "#2c5f7c");
        
        // Create tabs
        Tab searchTab = new Tab("Search");
        Tab browseTab = new Tab("Browse Articles");
        Tab authorsTab = new Tab("Authors");
        Tab statsTab = new Tab("Statistics");
        
        tabs = new Tabs(searchTab, browseTab, authorsTab, statsTab);
        tabs.setWidthFull();
        
        // Content area
        contentArea = new VerticalLayout();
        contentArea.setSizeFull();
        contentArea.setPadding(true);
        
        // Tab selection listener
        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();
            updateContent(selectedTab);
        });
        
        // Add components
        add(header, tabs, contentArea);
        
        // Show initial content
        updateContent(searchTab);
    }
    
    private void updateContent(Tab selectedTab) {
        contentArea.removeAll();
        
        String label = selectedTab.getLabel();
        
        if ("Search".equals(label)) {
            contentArea.add(createSearchView());
        } else if ("Browse Articles".equals(label)) {
            contentArea.add(createBrowseView());
        } else if ("Authors".equals(label)) {
            contentArea.add(createAuthorsView());
        } else if ("Statistics".equals(label)) {
            contentArea.add(createStatsView());
        }
    }
    
    private VerticalLayout createSearchView() {
        VerticalLayout layout = new VerticalLayout();
        layout.add(new H1("Search for Articles"));
        layout.add("Search functionality will be implemented here.");
        return layout;
    }
    
    private VerticalLayout createBrowseView() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        
        // Filter controls
        TextField titleFilter = new TextField("Title");
        titleFilter.setPlaceholder("Filter by title...");
        
        Button refreshBtn = new Button("Refresh");
        refreshBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        
        HorizontalLayout controls = new HorizontalLayout(titleFilter, refreshBtn, deleteBtn);
        controls.setDefaultVerticalComponentAlignment(Alignment.END);
        
        // Articles grid
        Grid<Article> grid = new Grid<>(Article.class, false);
        grid.addColumn(Article::getId).setHeader("ID").setWidth("80px");
        grid.addColumn(Article::getTitle).setHeader("Title").setFlexGrow(1);
        grid.addColumn(Article::getPublicationYear).setHeader("Year").setWidth("100px");
        grid.addColumn(Article::getCitationCount).setHeader("Citations").setWidth("120px");
        grid.setSizeFull();
        
        // Load data
        refreshBtn.addClickListener(e -> loadArticles(grid));
        loadArticles(grid);
        
        layout.add(controls, grid);
        return layout;
    }
    
    private void loadArticles(Grid<Article> grid) {
        try {
            List<Article> articles = articleRepository.findAll();
            grid.setItems(articles);
        } catch (Exception e) {
            grid.setItems();
        }
    }
    
    private VerticalLayout createAuthorsView() {
        VerticalLayout layout = new VerticalLayout();
        layout.add(new H1("Manage Authors"));
        layout.add("Authors management will be implemented here.");
        return layout;
    }
    
    private VerticalLayout createStatsView() {
        VerticalLayout layout = new VerticalLayout();
        layout.add(new H1("Database Statistics"));
        
        try {
            List<Article> articles = articleRepository.findAll();
            layout.add("Total Articles: " + articles.size());
        } catch (Exception e) {
            layout.add("Error loading statistics: " + e.getMessage());
        }
        
        return layout;
    }
}
