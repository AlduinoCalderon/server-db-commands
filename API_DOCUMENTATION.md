# Scholar API - Complete REST API Documentation

## 🎯 Overview

This REST API provides two main functionalities:
1. **Database Management** (`/api/articles`) - CRUD operations for stored articles
2. **Live SerpAPI Search** (`/api/search`) - Real-time Google Scholar searches

**Base URL (Local)**: `http://localhost:8080`  
**Base URL (Production)**: `https://your-service.onrender.com`

---

## 📚 Database Management Endpoints (`/api/articles`)

### 1. Get All Articles
```http
GET /api/articles
```

**Description**: Retrieve all articles from database

**Response**: `200 OK`
```json
[
  {
    "id": 1,
    "title": "Machine Learning in Healthcare",
    "authors": ["John Smith", "Jane Doe"],
    "year": 2023,
    "citations": 45,
    "url": "https://example.com/paper",
    "abstract": "..."
  }
]
```

**Example**:
```bash
# PowerShell
Invoke-WebRequest -Uri "http://localhost:8080/api/articles"

# curl
curl http://localhost:8080/api/articles
```

---

### 2. Get Article by ID
```http
GET /api/articles/{id}
```

**Description**: Get a specific article by its database ID

**Parameters**:
- `id` (path) - Article ID

**Response**: `200 OK` or `404 Not Found`
```json
{
  "id": 1,
  "title": "Machine Learning in Healthcare",
  "authors": ["John Smith", "Jane Doe"],
  "year": 2023,
  "citations": 45
}
```

**Example**:
```bash
curl http://localhost:8080/api/articles/1
```

---

### 3. Search Articles (Database)
```http
GET /api/articles/search?author=&year=&minCitations=
```

**Description**: Search stored articles with filters

**Query Parameters**:
- `author` (optional) - Filter by author name
- `year` (optional) - Filter by publication year
- `minCitations` (optional) - Minimum citation count

**Response**: `200 OK`
```json
[
  {
    "id": 1,
    "title": "Deep Learning",
    "authors": ["Smith"],
    "year": 2023,
    "citations": 50
  }
]
```

**Examples**:
```bash
# Search by author
curl "http://localhost:8080/api/articles/search?author=Smith"

# Search by year and citations
curl "http://localhost:8080/api/articles/search?year=2023&minCitations=10"

# PowerShell
Invoke-WebRequest -Uri "http://localhost:8080/api/articles/search?author=Smith"
```

---

### 4. Import Articles from Google Scholar
```http
POST /api/articles/import
Content-Type: application/json
```

**Description**: Import articles from Google Scholar and save to database

**Request Body**:
```json
{
  "query": "machine learning",
  "maxResults": 10
}
```

**Response**: `200 OK`
```json
{
  "imported": 10,
  "failed": 0,
  "message": "Successfully imported 10 articles"
}
```

**Example**:
```bash
# curl
curl -X POST http://localhost:8080/api/articles/import \
  -H "Content-Type: application/json" \
  -d '{"query":"artificial intelligence","maxResults":5}'

# PowerShell
$body = @{
    query = "artificial intelligence"
    maxResults = 5
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/articles/import" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body
```

---

### 5. Delete Article
```http
DELETE /api/articles/{id}
```

**Description**: Delete a specific article from database

**Parameters**:
- `id` (path) - Article ID to delete

**Response**: `200 OK` or `404 Not Found`
```json
{
  "message": "Article deleted successfully"
}
```

**Example**:
```bash
curl -X DELETE http://localhost:8080/api/articles/1
```

---

### 6. Bulk Delete Articles
```http
DELETE /api/articles/batch
Content-Type: application/json
```

**Description**: Delete multiple articles at once

**Request Body**:
```json
{
  "ids": [1, 2, 3, 4, 5]
}
```

**Response**: `200 OK`
```json
{
  "deleted": 5,
  "message": "5 articles deleted successfully"
}
```

**Example**:
```bash
# curl
curl -X DELETE http://localhost:8080/api/articles/batch \
  -H "Content-Type: application/json" \
  -d '{"ids":[1,2,3]}'

# PowerShell
$body = @{ ids = @(1,2,3) } | ConvertTo-Json
Invoke-WebRequest -Uri "http://localhost:8080/api/articles/batch" `
    -Method DELETE `
    -ContentType "application/json" `
    -Body $body
```

---

### 7. Get Statistics
```http
GET /api/articles/stats
```

**Description**: Get statistics about stored articles

**Response**: `200 OK`
```json
{
  "totalArticles": 150,
  "totalCitations": 5420,
  "averageCitations": 36.13,
  "yearDistribution": {
    "2023": 45,
    "2022": 62,
    "2021": 43
  }
}
```

**Example**:
```bash
curl http://localhost:8080/api/articles/stats
```

---

### 8. Health Check (Articles)
```http
GET /api/articles/health
```

**Description**: Check article service health

**Response**: `200 OK`
```json
{
  "status": "UP",
  "timestamp": 1234567890
}
```

---

## 🔍 Live SerpAPI Search Endpoints (`/api/search`)

**NEW! Live search without storing to database**

### 1. Search Articles (Live)
```http
GET /api/search/articles?query=&maxResults=
```

**Description**: Search Google Scholar in real-time via SerpAPI

**Query Parameters**:
- `query` (required) - Search query string
- `maxResults` (optional) - Max results, 1-20 (default: 10)

**Response**: `200 OK`
```json
{
  "searchMetadata": {
    "id": "search_123",
    "status": "Success",
    "totalResults": 1000
  },
  "organicResults": [
    {
      "position": 1,
      "title": "Deep Learning for Computer Vision",
      "link": "https://arxiv.org/paper",
      "publicationInfo": {
        "authors": ["A Smith", "B Jones"],
        "summary": "Published in CVPR 2023 - Cited by 120"
      },
      "snippet": "This paper presents...",
      "inlineLinks": {
        "citedBy": "https://scholar.google.com/citations?id=...",
        "versions": 5
      }
    }
  ],
  "pagination": {
    "current": 1,
    "next": "https://serpapi.com/search?..."
  }
}
```

**Examples**:
```bash
# Basic search
curl "http://localhost:8080/api/search/articles?query=machine%20learning&maxResults=5"

# PowerShell
$query = [System.Web.HttpUtility]::UrlEncode("machine learning")
Invoke-WebRequest -Uri "http://localhost:8080/api/search/articles?query=$query&maxResults=5"
```

---

### 2. Search by Author (Live)
```http
GET /api/search/author?name=&maxResults=
```

**Description**: Find articles by a specific author in real-time

**Query Parameters**:
- `name` (required) - Author name
- `maxResults` (optional) - Max results, 1-20 (default: 10)

**Response**: Same as search articles

**Examples**:
```bash
# Search author's publications
curl "http://localhost:8080/api/search/author?name=Yann%20LeCun&maxResults=10"

# PowerShell
$author = [System.Web.HttpUtility]::UrlEncode("Geoffrey Hinton")
Invoke-WebRequest -Uri "http://localhost:8080/api/search/author?name=$author"
```

---

### 3. Search Citing Articles (Live)
```http
GET /api/search/citations?citesId=&maxResults=
```

**Description**: Find papers that cite a specific paper

**Query Parameters**:
- `citesId` (required) - Google Scholar citation ID
- `maxResults` (optional) - Max results, 1-20 (default: 10)

**Response**: Same as search articles

**Examples**:
```bash
# Find papers citing a specific work
curl "http://localhost:8080/api/search/citations?citesId=123456789&maxResults=10"
```

---

### 4. Paginated Search (Live)
```http
GET /api/search/paginated?query=&start=&pageSize=
```

**Description**: Search with pagination support

**Query Parameters**:
- `query` (required) - Search query string
- `start` (optional) - Starting index, 0-based (default: 0)
- `pageSize` (optional) - Results per page, 1-20 (default: 10)

**Response**: Same as search articles with pagination metadata

**Examples**:
```bash
# Get first page
curl "http://localhost:8080/api/search/paginated?query=AI&start=0&pageSize=10"

# Get second page
curl "http://localhost:8080/api/search/paginated?query=AI&start=10&pageSize=10"
```

---

### 5. Health Check (Search)
```http
GET /api/search/health
```

**Description**: Check search service health

**Response**: `200 OK`
```json
{
  "status": "UP",
  "service": "ScholarSearchService",
  "timestamp": 1234567890
}
```

---

## 🔧 Combined Use Cases

### Use Case 1: Search and Import
```bash
# 1. Search live to preview results
curl "http://localhost:8080/api/search/articles?query=quantum%20computing&maxResults=5"

# 2. If satisfied, import to database
curl -X POST http://localhost:8080/api/articles/import \
  -H "Content-Type: application/json" \
  -d '{"query":"quantum computing","maxResults":5}'

# 3. Verify in database
curl "http://localhost:8080/api/articles/search?author=quantum"
```

### Use Case 2: Explore Author's Work
```bash
# 1. Find author's publications
curl "http://localhost:8080/api/search/author?name=Andrew%20Ng&maxResults=10"

# 2. Import interesting ones
curl -X POST http://localhost:8080/api/articles/import \
  -H "Content-Type: application/json" \
  -d '{"query":"author:Andrew Ng machine learning","maxResults":10}'
```

### Use Case 3: Track Citations
```bash
# 1. Get article details (includes citation ID)
curl "http://localhost:8080/api/articles/1"

# 2. Find papers citing this work (use citesId from step 1)
curl "http://localhost:8080/api/search/citations?citesId=CITATION_ID&maxResults=20"
```

---

## 🌐 CORS Configuration

The API is configured to accept requests from:
- `http://localhost:*` (any port)
- `https://*.github.io` (GitHub Pages)

**Production**: Update CORS settings in `ScholarRestApiApplication.java` to restrict origins.

---

## 🔒 Authentication

**Current**: No authentication (open API)

**Recommended for Production**:
1. Add Spring Security
2. Use API keys or JWT tokens
3. Rate limiting per API key

---

## ⚙️ Environment Variables

Required for deployment:

```env
# Database
DB_HOST=your-mysql-host
DB_PORT=3306
DB_NAME=your-database
DB_USER=your-username
DB_PASSWORD=your-password
DB_MAX_POOL_SIZE=2
DB_MIN_IDLE=1

# SerpAPI
SERPAPI_KEY=your-serpapi-key

# Server
PORT=8080
```

---

## 📊 Response Codes

| Code | Meaning |
|------|---------|
| 200 | Success |
| 400 | Bad Request (invalid parameters) |
| 404 | Not Found (article doesn't exist) |
| 500 | Internal Server Error |
| 503 | Service Unavailable (SerpAPI error) |

---

## 🧪 Testing Endpoints

### Health Checks
```bash
# Test database service
curl http://localhost:8080/api/articles/health

# Test search service
curl http://localhost:8080/api/search/health

# Test Spring Boot actuator
curl http://localhost:8080/actuator/health
```

### Complete Workflow Test
```powershell
# 1. Check health
Invoke-WebRequest -Uri "http://localhost:8080/api/articles/health"

# 2. Search live (no database)
Invoke-WebRequest -Uri "http://localhost:8080/api/search/articles?query=AI&maxResults=3"

# 3. Import to database
$body = @{ query = "artificial intelligence"; maxResults = 5 } | ConvertTo-Json
Invoke-WebRequest -Uri "http://localhost:8080/api/articles/import" -Method POST -ContentType "application/json" -Body $body

# 4. View imported articles
Invoke-WebRequest -Uri "http://localhost:8080/api/articles"

# 5. Get statistics
Invoke-WebRequest -Uri "http://localhost:8080/api/articles/stats"
```

---

## 📝 Frontend Integration Example

```javascript
// config.js
const API_BASE_URL = 'https://your-service.onrender.com';

// Search live without saving
async function searchLive(query) {
    const response = await fetch(
        `${API_BASE_URL}/api/search/articles?query=${encodeURIComponent(query)}&maxResults=10`
    );
    return response.json();
}

// Import to database
async function importArticles(query, maxResults) {
    const response = await fetch(`${API_BASE_URL}/api/articles/import`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ query, maxResults })
    });
    return response.json();
}

// Get saved articles
async function getSavedArticles() {
    const response = await fetch(`${API_BASE_URL}/api/articles`);
    return response.json();
}

// Search saved articles
async function searchSaved(author, year, minCitations) {
    const params = new URLSearchParams();
    if (author) params.append('author', author);
    if (year) params.append('year', year);
    if (minCitations) params.append('minCitations', minCitations);
    
    const response = await fetch(`${API_BASE_URL}/api/articles/search?${params}`);
    return response.json();
}
```

---

## 🎯 Summary

**Total Endpoints**: 13

### Database Operations (8)
1. ✅ GET /api/articles - List all
2. ✅ GET /api/articles/{id} - Get one
3. ✅ GET /api/articles/search - Filter saved
4. ✅ POST /api/articles/import - Import from Scholar
5. ✅ DELETE /api/articles/{id} - Delete one
6. ✅ DELETE /api/articles/batch - Delete many
7. ✅ GET /api/articles/stats - Statistics
8. ✅ GET /api/articles/health - Health check

### Live SerpAPI Search (5)
1. ✅ GET /api/search/articles - Search Google Scholar
2. ✅ GET /api/search/author - Search by author
3. ✅ GET /api/search/citations - Find citing papers
4. ✅ GET /api/search/paginated - Paginated search
5. ✅ GET /api/search/health - Health check

---

**Created**: After adding SerpAPI live search endpoints  
**Compilation**: ✅ BUILD SUCCESS (42 source files)
