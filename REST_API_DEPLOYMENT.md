# REST API Backend Deployment Guide

## ✅ What's Been Done

### Backend Implementation (feature/rest-api-backend branch)
- ✅ Created Spring Boot 2.7.14 REST API application
- ✅ Implemented 8 REST endpoints for full CRUD operations
- ✅ Configured CORS for GitHub Pages integration
- ✅ Created Dockerfile for Render deployment
- ✅ Optimized database connection pool (maxPoolSize=2, minIdle=1)
- ✅ Excluded GUI package (no JavaFX dependencies)
- ✅ Compilation successful (BUILD SUCCESS)
- ✅ Committed to Git

## 📋 REST API Endpoints

### Database Management (`/api/articles`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/articles` | Get all articles |
| GET | `/api/articles/{id}` | Get single article by ID |
| GET | `/api/articles/search?author=&year=&minCitations=` | Search with filters |
| POST | `/api/articles/import` | Import from Google Scholar |
| DELETE | `/api/articles/{id}` | Delete single article |
| DELETE | `/api/articles/batch` | Delete multiple articles |
| GET | `/api/articles/stats` | Get statistics |
| GET | `/api/articles/health` | Health check |

### Live SerpAPI Search (`/api/search`) - **NEW!**
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/search/articles?query=&maxResults=` | Search Google Scholar (live) |
| GET | `/api/search/author?name=&maxResults=` | Search by author (live) |
| GET | `/api/search/citations?citesId=&maxResults=` | Find citing papers (live) |
| GET | `/api/search/paginated?query=&start=&pageSize=` | Paginated search |
| GET | `/api/search/health` | Search service health |

**See `API_DOCUMENTATION.md` for complete details and examples.**

### Example Requests

**Import Articles:**
```bash
curl -X POST http://localhost:8080/api/articles/import \
  -H "Content-Type: application/json" \
  -d '{"query":"machine learning","maxResults":10}'
```

**Search Articles:**
```bash
curl "http://localhost:8080/api/articles/search?author=Smith&minCitations=10"
```

**Get Statistics:**
```bash
curl http://localhost:8080/api/articles/stats
```

## 🧪 Step 1: Test Locally

### Option A: Maven
```powershell
# Run the application
mvn spring-boot:run

# In another terminal, test endpoints
curl http://localhost:8080/api/articles/health
curl http://localhost:8080/api/articles
```

### Option B: Build JAR and Run
```powershell
# Build
mvn clean package

# Run
java -jar target/scholar-api-1.0.0.jar
```

### Expected Output
```
Started ScholarRestApiApplication in X.XXX seconds
```

### Test Endpoints (PowerShell)
```powershell
# Health check
Invoke-WebRequest -Uri "http://localhost:8080/api/articles/health" | Select-Object -ExpandProperty Content

# Get all articles
Invoke-WebRequest -Uri "http://localhost:8080/api/articles" | Select-Object -ExpandProperty Content

# Import articles
$body = @{
    query = "artificial intelligence"
    maxResults = 5
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/articles/import" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body
```

## 🚀 Step 2: Deploy Backend to Render

### 2.1 Push to GitHub
```powershell
# Push the feature branch
git push origin feature/rest-api-backend

# Or merge to main first
git checkout main
git merge feature/rest-api-backend
git push origin main
```

### 2.2 Create Render Web Service

1. **Go to Render Dashboard**: https://dashboard.render.com/
2. **Click "New +"** → **Web Service**
3. **Connect GitHub Repository**: `server-db-commands`
4. **Configure Service**:
   - **Name**: `scholar-api-backend`
   - **Region**: Oregon (US West) or your preferred region
   - **Branch**: `feature/rest-api-backend` (or `main` if merged)
   - **Runtime**: `Docker`
   - **Instance Type**: Free
   
5. **Environment Variables**:
   ```
   DB_HOST=b8hf01kbyrytp5j07gyn-mysql.services.clever-cloud.com
   DB_PORT=3306
   DB_NAME=b8hf01kbyrytp5j07gyn
   DB_USER=u1z6auemjfiosymm
   DB_PASSWORD=[your password from .env]
   SERPAPI_KEY=[your SerpAPI key from .env]
   DB_MAX_POOL_SIZE=2
   DB_MIN_IDLE=1
   PORT=8080
   ```

6. **Advanced Settings**:
   - **Health Check Path**: `/actuator/health`
   - **Auto-Deploy**: Yes

7. **Click "Create Web Service"**

### 2.3 Wait for Deployment
- Initial build takes ~5-10 minutes (Maven dependencies download)
- Watch logs for "Started ScholarRestApiApplication"
- Get your Render URL: `https://scholar-api-backend.onrender.com` (example)

### 2.4 Test Deployed API
```powershell
# Test health endpoint
Invoke-WebRequest -Uri "https://your-service.onrender.com/actuator/health"

# Test API endpoint
Invoke-WebRequest -Uri "https://your-service.onrender.com/api/articles/health"
```

## 🌐 Step 3: Create Static Frontend

### 3.1 Create Frontend Structure
```
frontend/
├── index.html          # Main page
├── css/
│   └── style.css       # Styles (mobile-responsive)
├── js/
│   ├── config.js       # API configuration
│   ├── api.js          # REST API client
│   ├── app.js          # Main application logic
│   └── components/
│       ├── search.js   # Search & import component
│       ├── browse.js   # Browse articles component
│       └── stats.js    # Statistics component
└── README.md
```

### 3.2 Example: config.js
```javascript
// Configuration
const API_BASE_URL = window.location.hostname === 'localhost' 
    ? 'http://localhost:8080'
    : 'https://your-service.onrender.com';

const API_ENDPOINTS = {
    articles: `${API_BASE_URL}/api/articles`,
    search: `${API_BASE_URL}/api/articles/search`,
    import: `${API_BASE_URL}/api/articles/import`,
    stats: `${API_BASE_URL}/api/articles/stats`,
    health: `${API_BASE_URL}/api/articles/health`
};
```

### 3.3 Example: api.js
```javascript
// REST API Client
class ScholarAPI {
    static async getAllArticles() {
        const response = await fetch(API_ENDPOINTS.articles);
        if (!response.ok) throw new Error('Failed to fetch articles');
        return response.json();
    }

    static async searchArticles(author, year, minCitations) {
        const params = new URLSearchParams();
        if (author) params.append('author', author);
        if (year) params.append('year', year);
        if (minCitations) params.append('minCitations', minCitations);
        
        const response = await fetch(`${API_ENDPOINTS.search}?${params}`);
        if (!response.ok) throw new Error('Failed to search articles');
        return response.json();
    }

    static async importArticles(query, maxResults = 10) {
        const response = await fetch(API_ENDPOINTS.import, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ query, maxResults })
        });
        if (!response.ok) throw new Error('Failed to import articles');
        return response.json();
    }

    static async deleteArticle(id) {
        const response = await fetch(`${API_ENDPOINTS.articles}/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) throw new Error('Failed to delete article');
        return response.json();
    }

    static async getStatistics() {
        const response = await fetch(API_ENDPOINTS.stats);
        if (!response.ok) throw new Error('Failed to fetch statistics');
        return response.json();
    }
}
```

### 3.4 Example: index.html (Basic Structure)
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Scholar API - Article Manager</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <header>
        <h1>📚 Scholar API</h1>
        <p>Manage your research articles</p>
    </header>

    <nav>
        <button data-tab="search" class="active">Search & Import</button>
        <button data-tab="browse">Browse Articles</button>
        <button data-tab="stats">Statistics</button>
    </nav>

    <main>
        <div id="search-tab" class="tab-content active">
            <!-- Search and import form -->
        </div>
        
        <div id="browse-tab" class="tab-content">
            <!-- Articles list -->
        </div>
        
        <div id="stats-tab" class="tab-content">
            <!-- Statistics charts -->
        </div>
    </main>

    <script src="js/config.js"></script>
    <script src="js/api.js"></script>
    <script src="js/app.js"></script>
</body>
</html>
```

## 📦 Step 4: Deploy Frontend to GitHub Pages

### Option A: Same Repository (docs folder)
```powershell
# Create docs folder for GitHub Pages
mkdir docs
cd docs

# Create frontend files (index.html, css/, js/)
# ...

# Commit and push
git add docs/
git commit -m "feat: add static frontend for REST API"
git push origin main

# Enable GitHub Pages:
# 1. Go to repository Settings
# 2. Pages → Branch: main → /docs → Save
# 3. Wait ~1 minute
# 4. Access: https://[username].github.io/server-db-commands/
```

### Option B: Separate Repository
```powershell
# Create new repository: scholar-api-frontend
# Clone and add frontend files
# Push to main

# Enable GitHub Pages:
# Settings → Pages → Branch: main → / (root) → Save
# Access: https://[username].github.io/scholar-api-frontend/
```

## 🔗 Step 5: Connect Frontend to Backend

1. **Update `config.js`** with your Render URL:
   ```javascript
   const API_BASE_URL = 'https://your-service.onrender.com';
   ```

2. **Commit and push** changes

3. **Wait for GitHub Pages** to rebuild (~1 minute)

4. **Test end-to-end**:
   - Open GitHub Pages URL
   - Try importing articles
   - Browse and filter articles
   - Check statistics
   - Test on mobile device

## 🐛 Troubleshooting

### Backend Issues

**Problem: "max_user_connections exceeded"**
- Solution: Check `DB_MAX_POOL_SIZE=2` is set in Render environment variables

**Problem: "Failed to connect to database"**
- Solution: Verify all DB_* environment variables are correct
- Check Clever Cloud MySQL is running

**Problem: Render service won't start**
- Check logs in Render dashboard
- Verify Dockerfile builds locally: `docker build -t scholar-api .`

### Frontend Issues

**Problem: CORS errors**
- Solution: Backend already configured CORS for `*.github.io`
- Verify your GitHub Pages URL matches pattern

**Problem: API calls fail**
- Check `config.js` has correct Render URL
- Test backend directly: `curl https://your-service.onrender.com/api/articles/health`

**Problem: Import not working**
- Verify `SERPAPI_KEY` is set in Render environment variables
- Check Render logs for errors

## 📊 Monitoring

### Backend Health
- **Render Dashboard**: View logs, metrics, restarts
- **Health Endpoint**: `https://your-service.onrender.com/actuator/health`
- **Manual Test**: `https://your-service.onrender.com/api/articles/stats`

### Frontend
- **Browser DevTools**: Check Console and Network tabs
- **GitHub Pages Status**: Repository Settings → Pages

## 🎯 Next Steps

1. ✅ **Test locally** - Run backend and verify endpoints work
2. ⏳ **Deploy backend to Render** - Follow Step 2
3. ⏳ **Create static frontend** - Follow Step 3
4. ⏳ **Deploy frontend to GitHub Pages** - Follow Step 4
5. ⏳ **Test end-to-end** - Verify full workflow
6. ⏳ **Monitor and optimize** - Check logs, adjust pool size if needed

## 💡 Architecture Benefits

### Why This Approach?
- ✅ **Single backend instance** = No connection pool multiplication
- ✅ **Free hosting** for frontend (GitHub Pages)
- ✅ **Scalability** = Can add CDN for static assets
- ✅ **Separation** = Backend and frontend can evolve independently
- ✅ **Modern** = Industry-standard microservices pattern

### Resource Usage
- **Backend**: 1 Render instance (Free tier)
- **Database**: Clever Cloud MySQL (5 connections max)
- **Pool**: 2 connections max = Safe for free tier
- **Frontend**: GitHub Pages (unlimited bandwidth)

## 📝 Notes

- **Free tier limitations**:
  - Render spins down after 15 minutes of inactivity
  - First request after spindown takes ~30 seconds to wake up
  - Consider adding loading states in frontend

- **Database connections**:
  - Current pool: maxPoolSize=2, minIdle=1
  - Can adjust via Render environment variables
  - Monitor in Clever Cloud dashboard

- **CORS**:
  - Already configured for localhost and *.github.io
  - No additional changes needed

---

**Created**: After successful REST API backend implementation  
**Branch**: feature/rest-api-backend  
**Commit**: 0737063f
