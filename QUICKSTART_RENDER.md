# Quick Start - Render Deployment

## 🚀 Deploy to Render in 3 Steps

### Step 1: Push to GitHub
```bash
git add .
git commit -m "feat: add Vaadin web interface for Render deployment"
git push origin feature/vaadin-web-interface
```

### Step 2: Create Web Service on Render
1. Go to https://dashboard.render.com/
2. Click **"New +"** → **"Web Service"**
3. Connect your GitHub repository: `server-db-commands`
4. Render will auto-detect `render.yaml` configuration

### Step 3: Set Environment Variables
In Render dashboard, add these secret variables:
- `SERPAPI_KEY` = your_serpapi_key
- `DB_HOST` = your_mysql_host
- `DB_NAME` = your_database_name
- `DB_USER` = your_db_username
- `DB_PASSWORD` = your_db_password

**That's it!** Render will automatically:
- Build your Java application (`mvn clean package`)
- Deploy to cloud
- Assign URL: `https://scholar-api.onrender.com`

## 📱 Access Your Application

Once deployed, open in browser:
```
https://your-app-name.onrender.com
```

You'll see the **Vaadin web interface** with:
- ✅ Search Tab - Search articles by researcher or title
- ✅ Browse Tab - View, filter, and delete articles
- ✅ Authors Tab - Manage authors with soft delete
- ✅ Statistics Tab - Database analytics

## ⚙️ Configuration Files

All ready for production:
- ✅ `render.yaml` - Infrastructure as code
- ✅ `application.properties` - Spring Boot config
- ✅ `pom.xml` - Maven with Vaadin dependencies
- ✅ `.dockerignore` - Docker optimization

## 🔄 Continuous Deployment

Every time you push to `main`:
1. Render detects changes
2. Auto-builds and deploys
3. Zero downtime update

## 📊 Features Included

### CRUD Operations
- **Create**: Add articles via API search
- **Read**: Browse and filter articles/authors
- **Update**: Edit article details (coming soon)
- **Delete**: Soft delete with `deleted_at` timestamp

### Soft Delete
- Articles: `deleted_at` field in database
- Authors: `deleted_at` field in database
- Deleted items hidden but recoverable

### Production Features
- Spring Boot Actuator health checks
- HikariCP connection pooling
- Vaadin production mode
- Compressed responses
- Logging configuration

## 🛠️ Local Development

Run locally:
```bash
mvn spring-boot:run
```
Open: http://localhost:8080

Run JavaFX desktop version:
```bash
mvn javafx:run
```

## 📚 Documentation

- [Full Deployment Guide](RENDER_DEPLOYMENT.md)
- [Technical Report](docs/Technical%20Report.md)
- [Database Schema](docs/DATABASE_README.md)
