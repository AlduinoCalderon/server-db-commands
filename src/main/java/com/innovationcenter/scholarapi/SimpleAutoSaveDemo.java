package com.innovationcenter.scholarapi;

import com.innovationcenter.scholarapi.model.Article;
import com.innovationcenter.scholarapi.repository.ArticleRepository;
import com.innovationcenter.scholarapi.repository.impl.MySQLArticleRepository;
import com.innovationcenter.scholarapi.service.ArticleService;
import com.innovationcenter.scholarapi.service.ConfigurationService;
import com.innovationcenter.scholarapi.service.DatabaseService;
import com.innovationcenter.scholarapi.service.impl.DotenvConfigurationService;
import com.innovationcenter.scholarapi.service.impl.MySQLDatabaseService;

import java.util.List;

/**
 * Simple demonstration of automatic database saving functionality.
 * Creates test articles and shows how they are automatically saved and retrieved.
 */
public class SimpleAutoSaveDemo {
    
    public static void main(String[] args) {
        try {
            SimpleAutoSaveDemo demo = new SimpleAutoSaveDemo();
            demo.demonstrateAutoSave();
        } catch (Exception e) {
            System.err.println("❌ Demo failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void demonstrateAutoSave() throws Exception {
        System.out.println("🎯 AUTOMATIC DATABASE SAVING DEMONSTRATION");
        System.out.println("=" .repeat(60));
        
        // Initialize components
        ConfigurationService configService = new DotenvConfigurationService();
        DatabaseService databaseService = new MySQLDatabaseService(configService);
        
        // Initialize database schema
        System.out.println("🔧 Initializing database...");
        databaseService.initializeSchema();
        
        // Create repository and service
        ArticleRepository repository = new MySQLArticleRepository(databaseService);
        ArticleService articleService = new ArticleService(repository);
        
        // Test database connection
        System.out.println("🔗 Testing database connection...");
        databaseService.testConnection();
        
        // Create and save test articles (simulating API results)
        System.out.println("\n📚 Creating test articles (simulating API responses)...");
        
        Article article1 = new Article(
            "Deep Learning Applications in Computer Vision", // paperTitle
            "John Smith, Alice Johnson", // authors
            2023, // publicationYear
            "IEEE Computer Vision Journal", // journal
            "https://example.com/article1", // articleUrl
            "This article explores deep learning applications in computer vision.", // abstractText
            "scholar123", // googleScholarId
            45, // citationCount
            "cites456", // citesId
            "https://example.com/pdf1.pdf", // pdfUrl
            "IEEE Publisher" // publisher
        );
        
        Article article2 = new Article(
            "Machine Learning for Natural Language Processing", // paperTitle
            "Maria Garcia, Bob Wilson", // authors
            2024, // publicationYear
            "Journal of AI Research", // journal
            "https://example.com/article2", // articleUrl
            "Comprehensive survey of ML techniques in NLP.", // abstractText
            "scholar789", // googleScholarId
            67, // citationCount
            "cites101", // citesId
            "https://example.com/pdf2.pdf", // pdfUrl
            "AI Research Society" // publisher
        );
        
        Article article3 = new Article(
            "Quantum Computing Algorithms", // paperTitle
            "David Johnson, Sarah Lee", // authors
            2024, // publicationYear
            "Quantum Science Review", // journal
            "https://example.com/article3", // articleUrl
            "Novel algorithms for quantum computing systems.", // abstractText
            "scholar456", // googleScholarId
            23, // citationCount
            "cites789", // citesId
            null, // pdfUrl (not available)
            "Quantum Publications" // publisher
        );
        
        // Automatically save articles to database
        System.out.println("\n💾 Automatically saving articles to database...");
        
        Article saved1 = articleService.saveArticle(article1);
        System.out.println("✅ Saved: " + saved1.getPaperTitle() + " (ID: " + saved1.getId() + ")");
        
        Article saved2 = articleService.saveArticle(article2);
        System.out.println("✅ Saved: " + saved2.getPaperTitle() + " (ID: " + saved2.getId() + ")");
        
        Article saved3 = articleService.saveArticle(article3);
        System.out.println("✅ Saved: " + saved3.getPaperTitle() + " (ID: " + saved3.getId() + ")");
        
        // Demonstrate automatic retrieval
        System.out.println("\n📖 Automatically retrieving saved articles...");
        
        // Get total count
        long totalCount = articleService.getTotalArticleCount();
        System.out.println("📊 Total articles in database: " + totalCount);
        
        // Find by author
        System.out.println("\n🔍 Finding articles by author 'John Smith'...");
        List<Article> johnSmithArticles = articleService.findByAuthor("John Smith");
        for (Article article : johnSmithArticles) {
            System.out.println("   📄 " + article.getPaperTitle() + " (" + article.getPublicationYear() + ")");
        }
        
        // Find by year
        System.out.println("\n📅 Finding articles from 2024...");
        List<Article> articles2024 = articleService.findByYear(2024);
        for (Article article : articles2024) {
            System.out.println("   📄 " + article.getPaperTitle() + " by " + article.getAuthors());
        }
        
        // Find highly cited articles
        System.out.println("\n⭐ Finding highly cited articles (>30 citations)...");
        List<Article> highlyCited = articleService.findByCitationsGreaterThan(30);
        for (Article article : highlyCited) {
            System.out.println("   📈 " + article.getPaperTitle() + " (" + article.getCitationCount() + " citations)");
        }
        
        System.out.println("\n🎉 AUTOMATIC SAVE/RETRIEVE DEMONSTRATION COMPLETED!");
        System.out.println("   ✅ Articles were automatically saved to database");
        System.out.println("   ✅ Articles were automatically retrieved from database");
        System.out.println("   ✅ Database operations work seamlessly");
        System.out.println("=" .repeat(60));
    }
}