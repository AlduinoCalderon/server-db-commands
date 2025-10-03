package com.innovationcenter.scholarapi;

import com.innovationcenter.scholarapi.repository.ArticleRepository;
import com.innovationcenter.scholarapi.repository.SimpleAuthorRepository;
import com.innovationcenter.scholarapi.repository.impl.MySQLArticleRepository;
import com.innovationcenter.scholarapi.repository.impl.MySQLSimpleAuthorRepository;
import com.innovationcenter.scholarapi.service.*;
import com.innovationcenter.scholarapi.service.impl.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main Spring Boot application entry point for Vaadin web interface.
 * Configures all services and repositories as Spring beans.
 */
@SpringBootApplication
public class ScholarVaadinApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScholarVaadinApplication.class, args);
    }

    @Bean
    public ConfigurationService configurationService() {
        return new DotenvConfigurationService();
    }

    @Bean
    public DatabaseService databaseService(ConfigurationService configService) {
        return new MySQLDatabaseService(configService);
    }

    @Bean
    public ArticleRepository articleRepository(DatabaseService databaseService) {
        return new MySQLArticleRepository(databaseService);
    }

    @Bean
    public SimpleAuthorRepository authorRepository(DatabaseService databaseService) {
        return new MySQLSimpleAuthorRepository(databaseService);
    }

    @Bean
    public JsonParser jsonParser() {
        return new GoogleScholarJsonParser();
    }

    @Bean
    public ApiService apiService(ConfigurationService configService, JsonParser jsonParser) {
        return new GoogleScholarApiService(configService, jsonParser);
    }

    @Bean
    public ScholarSearchService scholarSearchService(ConfigurationService configService) {
        return new SerpApiScholarSearchService(configService);
    }

    @Bean
    public ArticleService articleService(
            ArticleRepository articleRepository,
            SimpleAuthorRepository authorRepository) {
        return new ArticleService(articleRepository, authorRepository);
    }

    @Bean
    public ArticleIntegrationService articleIntegrationService(
            ApiService apiService,
            ArticleRepository articleRepository) {
        return new ArticleIntegrationService(apiService, articleRepository);
    }
}
