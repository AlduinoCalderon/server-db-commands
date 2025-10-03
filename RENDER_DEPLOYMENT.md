# Render Deployment Guide

## Prerequisites
✅ Render account created and linked to GitHub repository

## Deployment Steps for Render

### 1. Create Web Service on Render

1. Go to [Render Dashboard](https://dashboard.render.com/)
2. Click "New +" → "Web Service"
3. Select `server-db-commands` repository
4. Configure:
   - **Name**: `scholar-api`
   - **Region**: Choose closest to you
   - **Branch**: `feature/vaadin-web-interface` (or `main` after merging)
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/server-db-commands-1.0.0.jar`

### 2. Configure Environment Variables

Add these environment variables in Render:

```
SERPAPI_KEY=your_serpapi_key_here
DB_HOST=your_mysql_host
DB_PORT=3306
DB_NAME=your_database_name
DB_USER=your_db_username
DB_PASSWORD=your_db_password
```

### 3. Set Java Version

Add build environment variable:
```
JAVA_VERSION=11
```

### 4. Database Setup (MySQL)

**Option A: Use existing CleverCloud MySQL**
- Already configured in your `.env`
- Use same credentials in Render environment variables

**Option B: Add Render PostgreSQL** (recommended for Render)
1. In Render dashboard → "New +" → "PostgreSQL"
2. Get connection URL
3. Update code to support PostgreSQL (add dependency)

### 5. Deploy

1. Click "Create Web Service"
2. Render will automatically:
   - Pull code from GitHub
   - Run `mvn clean package`
   - Start the application
   - Assign a URL: `https://scholar-api.onrender.com`

### 6. Health Check (Optional)

Add health check endpoint to verify deployment:
- **Path**: `/actuator/health` (Spring Boot Actuator)
- Add dependency to `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

## Accessing Your Application

Once deployed:
- **URL**: `https://your-app-name.onrender.com`
- Open in any browser
- See Vaadin web interface with 4 tabs

## Common Issues & Solutions

### Issue: Build Timeout
**Solution**: 
- First build takes longer (downloads dependencies)
- Increase timeout in Render settings
- Or use Render's build cache

### Issue: Database Connection
**Solution**:
- Verify environment variables are set correctly
- Check database allows external connections
- Whitelist Render's IP ranges

### Issue: Memory Limits
**Solution**:
- Free tier: 512MB RAM
- Upgrade to paid plan if needed
- Optimize JVM memory: `-Xmx400m -Xms256m`

## Production Optimizations

### 1. Update `application.properties`

Create `src/main/resources/application.properties`:
```properties
# Server
server.port=${PORT:8080}
server.compression.enabled=true

# Vaadin Production Mode
vaadin.productionMode=true

# Logging
logging.level.root=INFO
logging.level.com.innovationcenter=INFO

# Database Pool
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
```

### 2. Add Dockerfile (Optional)

Create `Dockerfile` for custom container:
```dockerfile
FROM maven:3.8.6-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/server-db-commands-1.0.0.jar app.jar
EXPOSE 8080
CMD ["java", "-Xmx400m", "-jar", "app.jar"]
```

### 3. Add `render.yaml` (Infrastructure as Code)

Create `render.yaml` in project root:
```yaml
services:
  - type: web
    name: scholar-api
    env: java
    buildCommand: mvn clean package -DskipTests
    startCommand: java -Xmx400m -jar target/server-db-commands-1.0.0.jar
    envVars:
      - key: JAVA_VERSION
        value: 11
      - key: SERPAPI_KEY
        sync: false
      - key: DB_HOST
        sync: false
      - key: DB_PORT
        value: 3306
      - key: DB_NAME
        sync: false
      - key: DB_USER
        sync: false
      - key: DB_PASSWORD
        sync: false
    healthCheckPath: /actuator/health
```

## Continuous Deployment

Render automatically deploys when you push to GitHub:
1. Push to `main` branch (or configured branch)
2. Render detects changes
3. Automatically builds and deploys
4. Zero downtime deployment

## Monitoring

- **Logs**: View in Render dashboard
- **Metrics**: CPU, Memory, Response time
- **Alerts**: Configure email notifications

## Next Steps After Deployment

1. Test all features in production
2. Monitor logs for errors
3. Set up custom domain (optional)
4. Enable HTTPS (automatic on Render)
5. Configure caching for better performance
