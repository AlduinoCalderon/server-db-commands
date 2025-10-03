package com.innovationcenter.scholarapi.gui;

import com.innovationcenter.scholarapi.repository.ArticleRepository;
import com.innovationcenter.scholarapi.repository.SimpleAuthorRepository;
import com.innovationcenter.scholarapi.repository.impl.MySQLArticleRepository;
import com.innovationcenter.scholarapi.repository.impl.MySQLSimpleAuthorRepository;
import com.innovationcenter.scholarapi.service.ArticleService;
import com.innovationcenter.scholarapi.service.ConfigurationService;
import com.innovationcenter.scholarapi.service.DatabaseService;
import com.innovationcenter.scholarapi.service.ScholarSearchService;
import com.innovationcenter.scholarapi.service.impl.DotenvConfigurationService;
import com.innovationcenter.scholarapi.service.impl.MySQLDatabaseService;
import com.innovationcenter.scholarapi.service.impl.SerpApiScholarSearchService;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX GUI Application for Scholar API System.
 * Provides graphical interface for all console operations.
 */
public class ScholarGuiApplication extends Application {
    
    private ScholarSearchService searchService;
    private ArticleService articleService;
    
    @Override
    public void init() throws Exception {
        // Initialize services (same as console app)
        ConfigurationService configService = new DotenvConfigurationService();
        DatabaseService databaseService = new MySQLDatabaseService(configService);
        
        ArticleRepository articleRepository = new MySQLArticleRepository(databaseService);
        SimpleAuthorRepository authorRepository = new MySQLSimpleAuthorRepository(databaseService);
        
        searchService = new SerpApiScholarSearchService(configService);
        articleService = new ArticleService(articleRepository, authorRepository);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🎓 Scholar Article Management System");
        
        // Create main view
        ScholarMainView mainView = new ScholarMainView(searchService, articleService);
        
        Scene scene = new Scene(mainView.getView(), 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    @Override
    public void stop() throws Exception {
        // Cleanup resources if needed
        System.out.println("Application closing...");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
