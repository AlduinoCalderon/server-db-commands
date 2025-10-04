# Render Deployment Guide (PRIVATE - DO NOT COMMIT)

## 🚀 Quick Deployment Steps

### 1. Go to Render
- Open: https://dashboard.render.com/
- Sign in with GitHub

### 2. Create New Web Service
- Click **"New +"** → **"Web Service"**
- Connect repository: `AlduinoCalderon/server-db-commands`
- **Branch**: `feature/rest-api-backend`
- **Runtime**: **Docker**
- **Region**: Oregon (US West)
- **Instance Type**: Free

### 3. Add Environment Variables

⚠️ **Get values from your local .env file**

```powershell
# View your .env file locally
Get-Content .env
```

Add these in Render:
- `DB_HOST` = b8hf01kbyrytp5j07gyn-mysql.services.clever-cloud.com
- `DB_PORT` = 3306
- `DB_NAME` = b8hf01kbyrytp5j07gyn
- `DB_USER` = u1z6auemjfiosymm
- `DB_PASSWORD` = [FROM YOUR .ENV - KEEP SECRET]
- `SERP_API_KEY` = [FROM YOUR .ENV - KEEP SECRET] ⚠️ NOTE: SERP not SERPAPI
- `SERPAPI_KEY` = [FROM YOUR .ENV - KEEP SECRET] ⚠️ ADD BOTH VERSIONS
- `DB_MAX_POOL_SIZE` = 2
- `DB_MIN_IDLE` = 1
- `PORT` = 8080

### 4. Health Check
- **Health Check Path**: `/actuator/health`
- **Auto-Deploy**: Yes

### 5. Deploy
- Click **"Create Web Service"**
- Wait ~5-10 minutes

## ✅ Test After Deployment

```powershell
$API_URL = "https://server-db-commands-jmvz.onrender.com"

# 1. Health check (should work)
Invoke-WebRequest -Uri "$API_URL/actuator/health"

# 2. Test live search (requires SERP_API_KEY)
Invoke-WebRequest -Uri "$API_URL/api/search/articles?query=AI&maxResults=3"

# 3. Test database connection (requires DB_* variables)
Invoke-WebRequest -Uri "$API_URL/api/articles"

# 4. Test import (requires both)
$body = @{ query = "machine learning"; maxResults = 5 } | ConvertTo-Json
Invoke-WebRequest -Uri "$API_URL/api/articles/import" -Method POST -ContentType "application/json" -Body $body
```

## 🐛 Debugging 500 Errors

If endpoints return 500 errors:

1. **Check Render Logs:**
   - Dashboard → Your Service → Logs tab
   - Look for red error messages with stack traces
   - Common issues:
     * "NullPointerException" → Missing environment variable
     * "SQLException" → Database connection problem
     * "Connection refused" → Database credentials wrong
     * "API key" errors → SerpAPI key missing/invalid

2. **Verify Environment Variables:**
   - Make sure ALL variables are set in Render
   - ⚠️ **CRITICAL:** You need BOTH of these:
     * `SERP_API_KEY` (used by ConfigurationService)
     * `SERPAPI_KEY` (used by legacy code)
   
3. **Test in Order:**
   ```powershell
   # Start simple, then go complex
   
   # Step 1: Basic health (no dependencies)
   Invoke-WebRequest "$API_URL/actuator/health"
   
   # Step 2: Database endpoints (needs DB_*)
   Invoke-WebRequest "$API_URL/api/articles"
   
   # Step 3: Search endpoints (needs SERP_API_KEY)
   Invoke-WebRequest "$API_URL/api/search/articles?query=test&maxResults=1"
   ```

4. **If Logs Show Nothing:**
   - Application might not be starting at all
   - Check Render "Events" tab for build/deploy errors
   - Verify Docker build succeeded
   - Check if service is actually running

## 🔒 Security Notes
- Never commit .env file
- Never share DB_PASSWORD or SERPAPI_KEY
- This file should stay LOCAL only
