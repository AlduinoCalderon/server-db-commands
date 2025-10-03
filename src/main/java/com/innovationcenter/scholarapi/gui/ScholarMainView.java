package com.innovationcenter.scholarapi.gui;

import com.innovationcenter.scholarapi.model.Article;
import com.innovationcenter.scholarapi.model.SimpleAuthor;
import com.innovationcenter.scholarapi.model.ScholarSearchResponse;
import com.innovationcenter.scholarapi.service.ArticleService;
import com.innovationcenter.scholarapi.service.ScholarSearchService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.util.List;

/**
 * Main view for Scholar API GUI.
 * Contains tabs for: Search, Browse, Statistics, and Authors.
 */
public class ScholarMainView {
    
    private final ScholarSearchService searchService;
    private final ArticleService articleService;
    private final BorderPane rootPane;
    private final ObservableList<Article> articlesData;
    
    public ScholarMainView(ScholarSearchService searchService, ArticleService articleService) {
        this.searchService = searchService;
        this.articleService = articleService;
        this.articlesData = FXCollections.observableArrayList();
        this.rootPane = createMainView();
    }
    
    private BorderPane createMainView() {
        BorderPane pane = new BorderPane();
        
        // Header
        pane.setTop(createHeader());
        
        // Main content with tabs
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        tabPane.getTabs().addAll(
            createSearchTab(),
            createBrowseTab(),
            createAuthorsTab(),
            createStatisticsTab()
        );
        
        pane.setCenter(tabPane);
        
        // Status bar
        pane.setBottom(createStatusBar());
        
        return pane;
    }
    
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #2c3e50;");
        
        Label title = new Label("🎓 Scholar Article Management System");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: white;");
        
        Label subtitle = new Label("Search, manage, and analyze academic articles from Google Scholar");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setStyle("-fx-text-fill: #ecf0f1;");
        
        header.getChildren().addAll(title, subtitle);
        return header;
    }
    
    private Tab createSearchTab() {
        Tab tab = new Tab("🔍 Search Articles");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        // Search controls
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        
        Label searchLabel = new Label("Search by:");
        ComboBox<String> searchType = new ComboBox<>();
        searchType.getItems().addAll("Researcher Name", "Title (API)");
        searchType.setValue("Researcher Name");
        
        TextField searchField = new TextField();
        searchField.setPromptText("Enter search term...");
        searchField.setPrefWidth(300);
        
        Spinner<Integer> maxResults = new Spinner<>(1, 20, 10);
        maxResults.setPrefWidth(80);
        
        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Button clearButton = new Button("Clear");
        clearButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        
        searchBox.getChildren().addAll(
            searchLabel, searchType, 
            new Label("Term:"), searchField,
            new Label("Max:"), maxResults,
            searchButton, clearButton
        );
        
        // Progress indicator
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(Double.MAX_VALUE);
        progressBar.setVisible(false);
        
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #27ae60;");
        
        // Results table
        TableView<Article> table = createArticleTable();
        table.setItems(articlesData);
        VBox.setVgrow(table, Priority.ALWAYS);
        
        // Search button action
        searchButton.setOnAction(e -> {
            String term = searchField.getText().trim();
            if (term.isEmpty()) {
                showAlert("Input Required", "Please enter a search term.");
                return;
            }
            
            progressBar.setVisible(true);
            progressBar.setProgress(-1);
            searchButton.setDisable(true);
            statusLabel.setText("Searching...");
            
            // Run search in background thread
            new Thread(() -> {
                try {
                    List<Article> results = performSearch(
                        searchType.getValue(), 
                        term, 
                        maxResults.getValue()
                    );
                    
                    Platform.runLater(() -> {
                        articlesData.clear();
                        articlesData.addAll(results);
                        progressBar.setVisible(false);
                        searchButton.setDisable(false);
                        statusLabel.setText("✓ Found " + results.size() + " articles");
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        progressBar.setVisible(false);
                        searchButton.setDisable(false);
                        statusLabel.setText("✗ Error: " + ex.getMessage());
                        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                        showAlert("Search Error", ex.getMessage());
                    });
                }
            }).start();
        });
        
        // Clear button action
        clearButton.setOnAction(e -> {
            searchField.clear();
            articlesData.clear();
            statusLabel.setText("");
        });
        
        content.getChildren().addAll(searchBox, progressBar, statusLabel, table);
        tab.setContent(content);
        return tab;
    }
    
    private Tab createBrowseTab() {
        Tab tab = new Tab("📚 Browse Database");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        // Help text
        Label helpLabel = new Label("💡 Browse articles already stored in your database");
        helpLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        
        // Filter controls
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        Label filterLabel = new Label("Filter by:");
        ComboBox<String> filterType = new ComboBox<>();
        filterType.getItems().addAll("All Articles", "By Author", "By Year (and newer)", "Highly Cited (minimum)");
        filterType.setValue("All Articles");
        
        TextField filterValue = new TextField();
        filterValue.setPromptText("Filter is disabled");
        filterValue.setPrefWidth(250);
        filterValue.setDisable(true);
        
        // Hint label that changes based on selection
        Label hintLabel = new Label();
        hintLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #3498db;");
        
        Button loadButton = new Button("Load Articles");
        loadButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        
        filterType.setOnAction(e -> {
            String selected = filterType.getValue();
            filterValue.setDisable(selected.equals("All Articles"));
            
            if (selected.equals("By Author")) {
                filterValue.setPromptText("Enter author name (e.g., John Smith)");
                hintLabel.setText("ℹ️ Tip: Use partial names to find multiple authors (e.g., 'Smith')");
            } else if (selected.equals("By Year (and newer)")) {
                filterValue.setPromptText("Enter year (e.g., 2020)");
                hintLabel.setText("ℹ️ Tip: Will show all articles from this year onwards (e.g., 2020 → 2020-2025)");
            } else if (selected.equals("Highly Cited (minimum)")) {
                filterValue.setPromptText("Minimum citations (e.g., 50)");
                hintLabel.setText("ℹ️ Tip: Enter a number to see articles with at least that many citations");
            } else {
                filterValue.setPromptText("Filter is disabled");
                hintLabel.setText("");
            }
        });
        
        filterBox.getChildren().addAll(filterLabel, filterType, filterValue, loadButton);
        
        // Results table
        TableView<Article> table = createArticleTable();
        VBox.setVgrow(table, Priority.ALWAYS);
        
        Label countLabel = new Label();
        countLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        // CRUD Action buttons
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        actionBox.setPadding(new Insets(10, 0, 0, 0));
        
        Button viewButton = new Button("📄 View Details");
        viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        viewButton.setDisable(true);
        
        Button deleteButton = new Button("🗑️ Delete");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteButton.setDisable(true);
        
        Button refreshButton = new Button("🔄 Refresh");
        refreshButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        
        actionBox.getChildren().addAll(viewButton, deleteButton, refreshButton);
        
        // Enable/disable buttons based on selection
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            viewButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
        });
        
        // View button action
        viewButton.setOnAction(e -> {
            Article selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showArticleDetails(selected);
            }
        });
        
        // Delete button action
        deleteButton.setOnAction(e -> {
            Article selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirm Delete");
                confirm.setHeaderText("Delete Article?");
                confirm.setContentText("Are you sure you want to delete:\n\"" + selected.getPaperTitle() + "\"?\n\nThis will perform a soft delete (can be restored).");
                
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        deleteArticle(selected, table, countLabel);
                    }
                });
            }
        });
        
        // Refresh button action
        refreshButton.setOnAction(e -> loadButton.fire());
        
        // Load button action
        loadButton.setOnAction(e -> {
            loadButton.setDisable(true);
            
            new Thread(() -> {
                try {
                    List<Article> articles = loadArticles(filterType.getValue(), filterValue.getText());
                    
                    Platform.runLater(() -> {
                        table.setItems(FXCollections.observableArrayList(articles));
                        countLabel.setText("Loaded " + articles.size() + " articles");
                        loadButton.setDisable(false);
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        showAlert("Load Error", ex.getMessage());
                        loadButton.setDisable(false);
                    });
                }
            }).start();
        });
        
        content.getChildren().addAll(helpLabel, filterBox, hintLabel, countLabel, table, actionBox);
        tab.setContent(content);
        return tab;
    }
    
    private Tab createStatisticsTab() {
        Tab tab = new Tab("📊 Statistics");
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        Label title = new Label("Database Statistics");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(15);
        statsGrid.setPadding(new Insets(20));
        statsGrid.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 5;");
        
        Label totalArticlesLabel = new Label("Total Articles:");
        totalArticlesLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label totalArticlesValue = new Label("--");
        totalArticlesValue.setFont(Font.font("Arial", 14));
        
        Label totalAuthorsLabel = new Label("Total Authors:");
        totalAuthorsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label totalAuthorsValue = new Label("--");
        totalAuthorsValue.setFont(Font.font("Arial", 14));
        
        statsGrid.add(totalArticlesLabel, 0, 0);
        statsGrid.add(totalArticlesValue, 1, 0);
        statsGrid.add(totalAuthorsLabel, 0, 1);
        statsGrid.add(totalAuthorsValue, 1, 1);
        
        Button refreshButton = new Button("Refresh Statistics");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        
        refreshButton.setOnAction(e -> {
            new Thread(() -> {
                try {
                    long articleCount = articleService.getTotalArticleCount();
                    
                    Platform.runLater(() -> {
                        totalArticlesValue.setText(String.valueOf(articleCount));
                        totalAuthorsValue.setText("--"); // Will implement when author methods are available
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> showAlert("Statistics Error", ex.getMessage()));
                }
            }).start();
        });
        
        content.getChildren().addAll(title, statsGrid, refreshButton);
        tab.setContent(content);
        return tab;
    }
    
    private Tab createAuthorsTab() {
        Tab tab = new Tab("👥 Authors Database");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        // Help text
        Label helpLabel = new Label("💡 Browse and search authors extracted from your article database");
        helpLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        
        // Search/Filter controls
        HBox controlBox = new HBox(10);
        controlBox.setAlignment(Pos.CENTER_LEFT);
        
        Label filterLabel = new Label("View:");
        ComboBox<String> filterType = new ComboBox<>();
        filterType.getItems().addAll(
            "All Authors", 
            "Search by Name", 
            "Top by Citations", 
            "Top by Article Count"
        );
        filterType.setValue("All Authors");
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search is disabled");
        searchField.setPrefWidth(250);
        searchField.setDisable(true);
        
        Spinner<Integer> topLimit = new Spinner<>(5, 100, 20, 5);
        topLimit.setPrefWidth(80);
        topLimit.setVisible(false);
        Label topLabel = new Label("Limit:");
        topLabel.setVisible(false);
        
        // Hint label
        Label hintLabel = new Label();
        hintLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #3498db;");
        
        Button loadButton = new Button("Load Authors");
        loadButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");
        
        filterType.setOnAction(e -> {
            String selected = filterType.getValue();
            boolean isSearch = selected.equals("Search by Name");
            boolean isTop = selected.contains("Top");
            
            searchField.setDisable(!isSearch);
            topLimit.setVisible(isTop);
            topLabel.setVisible(isTop);
            
            if (selected.equals("All Authors")) {
                searchField.setPromptText("Search is disabled");
                hintLabel.setText("ℹ️ Tip: Shows all authors found in your database");
            } else if (isSearch) {
                searchField.setPromptText("Enter author name (e.g., Smith)");
                hintLabel.setText("ℹ️ Tip: Partial names work too (e.g., 'John' finds 'John Smith', 'Johnny', etc.)");
            } else if (selected.equals("Top by Citations")) {
                hintLabel.setText("ℹ️ Tip: Shows authors with the most total citations across all their papers");
            } else if (selected.equals("Top by Article Count")) {
                hintLabel.setText("ℹ️ Tip: Shows the most prolific authors by number of papers");
            }
        });
        
        controlBox.getChildren().addAll(
            filterLabel, filterType, 
            searchField, 
            topLabel, topLimit,
            loadButton
        );
        
        // Authors table
        TableView<SimpleAuthor> authorTable = createAuthorTable();
        VBox.setVgrow(authorTable, Priority.ALWAYS);
        
        Label countLabel = new Label();
        countLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        // CRUD Action buttons for authors
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        actionBox.setPadding(new Insets(10, 0, 0, 0));
        
        Button viewAuthorButton = new Button("📊 View Author Stats");
        viewAuthorButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
        viewAuthorButton.setDisable(true);
        
        Button deleteAuthorButton = new Button("🗑️ Delete Author");
        deleteAuthorButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteAuthorButton.setDisable(true);
        
        Button refreshAuthorsButton = new Button("🔄 Refresh");
        refreshAuthorsButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        
        actionBox.getChildren().addAll(viewAuthorButton, deleteAuthorButton, refreshAuthorsButton);
        
        // Enable/disable buttons based on selection
        authorTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            viewAuthorButton.setDisable(!hasSelection);
            deleteAuthorButton.setDisable(!hasSelection);
        });
        
        // View author stats button
        viewAuthorButton.setOnAction(e -> {
            SimpleAuthor selected = authorTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showAuthorDetails(selected);
            }
        });
        
        // Delete author button
        deleteAuthorButton.setOnAction(e -> {
            SimpleAuthor selected = authorTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirm Delete");
                confirm.setHeaderText("Delete Author?");
                confirm.setContentText("Are you sure you want to delete author:\n\"" + selected.getFullName() + "\"?\n\nThis will perform a soft delete (can be restored).");
                
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        deleteAuthor(selected, authorTable, countLabel);
                    }
                });
            }
        });
        
        // Refresh button
        refreshAuthorsButton.setOnAction(e -> loadButton.fire());
        
        // Load button action
        loadButton.setOnAction(e -> {
            loadButton.setDisable(true);
            
            new Thread(() -> {
                try {
                    List<SimpleAuthor> authors = loadAuthors(
                        filterType.getValue(), 
                        searchField.getText(),
                        topLimit.getValue()
                    );
                    
                    Platform.runLater(() -> {
                        authorTable.setItems(FXCollections.observableArrayList(authors));
                        countLabel.setText("📊 Loaded " + authors.size() + " author(s)");
                        loadButton.setDisable(false);
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        showAlert("Load Error", ex.getMessage());
                        loadButton.setDisable(false);
                    });
                }
            }).start();
        });
        
        content.getChildren().addAll(helpLabel, controlBox, hintLabel, countLabel, authorTable, actionBox);
        tab.setContent(content);
        return tab;
    }
    
    private TableView<SimpleAuthor> createAuthorTable() {
        TableView<SimpleAuthor> table = new TableView<>();
        
        TableColumn<SimpleAuthor, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        
        TableColumn<SimpleAuthor, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        nameCol.setPrefWidth(250);
        
        TableColumn<SimpleAuthor, Integer> articlesCol = new TableColumn<>("Articles");
        articlesCol.setCellValueFactory(new PropertyValueFactory<>("articleCount"));
        articlesCol.setPrefWidth(80);
        
        TableColumn<SimpleAuthor, Integer> citationsCol = new TableColumn<>("Total Citations");
        citationsCol.setCellValueFactory(new PropertyValueFactory<>("totalCitations"));
        citationsCol.setPrefWidth(120);
        
        TableColumn<SimpleAuthor, String> avgCol = new TableColumn<>("Avg Citations");
        avgCol.setCellValueFactory(cellData -> {
            SimpleAuthor author = cellData.getValue();
            double avg = author.getAverageCitations();
            return new javafx.beans.property.SimpleStringProperty(
                String.format("%.2f", avg)
            );
        });
        avgCol.setPrefWidth(120);
        
        TableColumn<SimpleAuthor, java.sql.Timestamp> firstSeenCol = new TableColumn<>("First Seen");
        firstSeenCol.setCellValueFactory(new PropertyValueFactory<>("firstSeen"));
        firstSeenCol.setPrefWidth(150);
        
        table.getColumns().addAll(idCol, nameCol, articlesCol, citationsCol, avgCol, firstSeenCol);
        
        return table;
    }
    
    private List<SimpleAuthor> loadAuthors(String filterType, String searchValue, int limit) throws SQLException {
        switch (filterType) {
            case "All Authors":
                // Get all authors - we'll need to add a findAll method or use findTopByArticleCount with high limit
                return articleService.getTopAuthorsByArticleCount(1000);
            case "Search by Name":
                if (searchValue.isEmpty()) {
                    throw new IllegalArgumentException("Please enter author name to search");
                }
                return articleService.searchAuthors(searchValue);
            case "Top by Citations":
                return articleService.getTopAuthorsByCitations(limit);
            case "Top by Article Count":
                return articleService.getTopAuthorsByArticleCount(limit);
            default:
                return List.of();
        }
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(10));
        statusBar.setStyle("-fx-background-color: #34495e;");
        
        Label statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: white;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label versionLabel = new Label("v1.0.0");
        versionLabel.setStyle("-fx-text-fill: #95a5a6;");
        
        statusBar.getChildren().addAll(statusLabel, spacer, versionLabel);
        return statusBar;
    }
    
    private TableView<Article> createArticleTable() {
        TableView<Article> table = new TableView<>();
        
        TableColumn<Article, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        
        TableColumn<Article, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("paperTitle"));
        titleCol.setPrefWidth(300);
        
        TableColumn<Article, String> authorsCol = new TableColumn<>("Authors");
        authorsCol.setCellValueFactory(new PropertyValueFactory<>("authors"));
        authorsCol.setPrefWidth(200);
        
        TableColumn<Article, Integer> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("publicationYear"));
        yearCol.setPrefWidth(60);
        
        TableColumn<Article, String> journalCol = new TableColumn<>("Journal");
        journalCol.setCellValueFactory(new PropertyValueFactory<>("journal"));
        journalCol.setPrefWidth(150);
        
        TableColumn<Article, Integer> citationsCol = new TableColumn<>("Citations");
        citationsCol.setCellValueFactory(new PropertyValueFactory<>("citationCount"));
        citationsCol.setPrefWidth(80);
        
        table.getColumns().addAll(idCol, titleCol, authorsCol, yearCol, journalCol, citationsCol);
        
        return table;
    }
    
    private List<Article> performSearch(String searchType, String term, int maxResults) throws Exception {
        ScholarSearchResponse response;
        
        switch (searchType) {
            case "Researcher Name":
                response = searchService.searchByAuthor(term, maxResults);
                break;
            case "Title (API)":
                // Use the general search endpoint for titles
                response = searchService.searchArticles(term, maxResults);
                break;
            default:
                throw new IllegalArgumentException("Unknown search type: " + searchType);
        }
        
        if (response == null || response.getOrganicResults() == null) {
            return List.of();
        }
        
        return articleService.processSearchResponse(response, maxResults);
    }
    
    private List<Article> loadArticles(String filterType, String filterValue) throws SQLException {
        switch (filterType) {
            case "All Articles":
                // Get all articles from database
                List<Article> allArticles = articleService.findByCitationsGreaterThan(-1);
                return allArticles;
            case "By Author":
                if (filterValue.isEmpty()) throw new IllegalArgumentException("Please enter author name");
                return articleService.findByAuthor(filterValue);
            case "By Year (and newer)":
                if (filterValue.isEmpty()) throw new IllegalArgumentException("Please enter year");
                int year = Integer.parseInt(filterValue);
                // Get all articles and filter for year >= specified year
                List<Article> yearArticles = articleService.findByCitationsGreaterThan(-1);
                return yearArticles.stream()
                    .filter(a -> a.getPublicationYear() != null && a.getPublicationYear() >= year)
                    .sorted((a, b) -> Integer.compare(b.getPublicationYear(), a.getPublicationYear()))
                    .collect(java.util.stream.Collectors.toList());
            case "Highly Cited (minimum)":
                if (filterValue.isEmpty()) throw new IllegalArgumentException("Please enter minimum citations");
                return articleService.findByCitationsGreaterThan(Integer.parseInt(filterValue));
            default:
                return List.of();
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showArticleDetails(Article article) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Article Details");
        dialog.setHeaderText(article.getPaperTitle());
        
        StringBuilder content = new StringBuilder();
        content.append("📋 ID: ").append(article.getId()).append("\n\n");
        content.append("👥 Authors: ").append(article.getAuthors()).append("\n\n");
        content.append("📅 Year: ").append(article.getPublicationYear()).append("\n\n");
        content.append("📚 Journal: ").append(article.getJournal() != null ? article.getJournal() : "N/A").append("\n\n");
        content.append("📈 Citations: ").append(article.getCitationCount()).append("\n\n");
        content.append("🔗 URL: ").append(article.getArticleUrl() != null ? article.getArticleUrl() : "N/A").append("\n\n");
        content.append("🆔 Scholar ID: ").append(article.getGoogleScholarId() != null ? article.getGoogleScholarId() : "N/A").append("\n\n");
        
        if (article.getAbstractText() != null && !article.getAbstractText().isEmpty()) {
            content.append("📄 Abstract:\n").append(article.getAbstractText());
        }
        
        dialog.setContentText(content.toString());
        dialog.getDialogPane().setPrefWidth(600);
        dialog.showAndWait();
    }
    
    private void deleteArticle(Article article, TableView<Article> table, Label countLabel) {
        new Thread(() -> {
            try {
                boolean success = articleService.deleteArticle(article.getId());
                
                Platform.runLater(() -> {
                    if (success) {
                        table.getItems().remove(article);
                        countLabel.setText("✓ Deleted! Now showing " + table.getItems().size() + " articles");
                        showAlert("Success", "Article deleted successfully (soft delete)");
                    } else {
                        showAlert("Error", "Failed to delete article");
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    showAlert("Delete Error", "Error: " + ex.getMessage());
                });
            }
        }).start();
    }
    
    private void showAuthorDetails(SimpleAuthor author) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Author Statistics");
        dialog.setHeaderText("📊 " + author.getFullName());
        
        StringBuilder content = new StringBuilder();
        content.append("🆔 ID: ").append(author.getId()).append("\n\n");
        content.append("📚 Total Articles: ").append(author.getArticleCount()).append("\n\n");
        content.append("📈 Total Citations: ").append(author.getTotalCitations()).append("\n\n");
        content.append("⭐ Average Citations per Article: ").append(String.format("%.2f", author.getAverageCitations())).append("\n\n");
        content.append("🕐 First Seen: ").append(author.getFirstSeen()).append("\n\n");
        content.append("🕑 Last Updated: ").append(author.getLastUpdated()).append("\n\n");
        
        // Note: Would need to implement a method to get articles by author ID
        // For now, just show the statistics
        
        dialog.setContentText(content.toString());
        dialog.getDialogPane().setPrefWidth(600);
        dialog.showAndWait();
    }
    
    private void deleteAuthor(SimpleAuthor author, TableView<SimpleAuthor> table, Label countLabel) {
        new Thread(() -> {
            try {
                // Call the service to soft delete the author
                boolean success = articleService.deleteAuthor(author.getId());
                
                Platform.runLater(() -> {
                    if (success) {
                        // Remove from table
                        table.getItems().remove(author);
                        countLabel.setText("✓ Deleted! Now showing " + table.getItems().size() + " authors");
                        
                        // Show success message
                        showAlert("Success", "Author '" + author.getFullName() + "' has been soft-deleted.\n\n" +
                            "The author is marked as deleted but can be restored if needed.");
                    } else {
                        showAlert("Delete Failed", "Author could not be deleted. It may have already been deleted.");
                    }
                });
                
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    showAlert("Delete Error", "Error: " + ex.getMessage());
                });
            }
        }).start();
    }
    
    public BorderPane getView() {
        return rootPane;
    }
}
