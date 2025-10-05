package com.innovationcenter.scholarapi.api;

import com.innovationcenter.scholarapi.repository.impl.MySQLArticleRepository;
import com.innovationcenter.scholarapi.repository.impl.MySQLSimpleAuthorRepository;
import com.innovationcenter.scholarapi.service.ArticleService;
import com.innovationcenter.scholarapi.service.ConfigurationService;
import com.innovationcenter.scholarapi.service.DatabaseService;
import com.innovationcenter.scholarapi.service.DatabaseServiceFactory;
import com.innovationcenter.scholarapi.service.impl.DotenvConfigurationService;
import io.javalin.Javalin;

/**
 * Small main to start the API and serve a minimal frontend web page.
 */
public class ApiMain {
	public static void main(String[] args) throws Exception {
		ConfigurationService configService = new DotenvConfigurationService();
		DatabaseService db = DatabaseServiceFactory.create(configService);

		MySQLArticleRepository articleRepo = new MySQLArticleRepository(db);
		MySQLSimpleAuthorRepository authorRepo = new MySQLSimpleAuthorRepository(db);

		ArticleService articleService = new ArticleService(articleRepo, authorRepo);

		// Create a single Javalin instance that serves static files from src/main/resources/static
		Javalin app = Javalin.create(config -> {
			config.staticFiles.add(staticFiles -> {
				staticFiles.hostedPath = "/";
				staticFiles.directory = "/static"; // resource path inside classpath
				staticFiles.location = io.javalin.http.staticfiles.Location.CLASSPATH;
			});
		});

		ApiServer server = new ApiServer(app, articleService, configService);

		app.start(7000);
		System.out.println("Frontend available at http://localhost:7000/index.html");
	}
}
