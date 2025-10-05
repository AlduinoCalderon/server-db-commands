package com.innovationcenter.scholarapi.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationcenter.scholarapi.model.Article;
import com.innovationcenter.scholarapi.service.ArticleService;
import com.innovationcenter.scholarapi.service.ConfigurationService;
import com.innovationcenter.scholarapi.service.DatabaseService;
import com.innovationcenter.scholarapi.service.DatabaseServiceFactory;
import io.javalin.Javalin;
import io.javalin.http.Handler;

import java.util.List;

/**
 * Simple REST API using Javalin to expose basic article/author operations.
 * This uses existing services (ArticleService) — you need to wire a concrete
 * ArticleService implementation when starting the API (e.g., from your app main).
 */
public class ApiServer {

	private final Javalin app;
	private final ObjectMapper mapper = new ObjectMapper();

	public ApiServer(Javalin app, ArticleService articleService, ConfigurationService configService) {
		DatabaseService databaseService = DatabaseServiceFactory.create(configService);

		this.app = app;

		Handler getAll = ctx -> {
			try {
				List<Article> articles = articleService.findByCitationsGreaterThan(-1);
				ctx.json(articles);
			} catch (Exception e) {
				ctx.status(500).result("Error: " + e.getMessage());
			}
		};

		Handler getById = ctx -> {
			try {
				long id = Long.parseLong(ctx.pathParam("id"));
				Article found = articleService.findById(id);
				if (found == null) ctx.status(404).result("Not found"); else ctx.json(found);
			} catch (NumberFormatException ne) {
				ctx.status(400).result("Invalid id");
			} catch (Exception e) {
				ctx.status(500).result("Error: " + e.getMessage());
			}
		};

		Handler deleteById = ctx -> {
			try {
				long id = Long.parseLong(ctx.pathParam("id"));
				boolean ok = articleService.deleteArticle(id);
				if (ok) ctx.status(200).result("Deleted"); else ctx.status(404).result("Not found or not deleted");
			} catch (NumberFormatException ne) {
				ctx.status(400).result("Invalid id");
			} catch (Exception e) {
				ctx.status(500).result("Error: " + e.getMessage());
			}
		};

		app.get("/articles", getAll);
		app.get("/articles/{id}", getById);
		app.delete("/articles/{id}", deleteById);

		// You can add authors endpoints similarly using author service / repository
	}

	public void start(int port) {
		app.start(port);
		System.out.println("API started on port " + port);
	}

	public void stop() {
		app.stop();
	}
}
