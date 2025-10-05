# Server and Database Commands

> Automating Researcher Information Integration with Google Scholar API

[![Project Status](https://img.shields.io/badge/status-in%20development-yellow)](https://github.com/AlduinoCalderon/server-db-commands)
[![Sprint](https://img.shields.io/badge/sprint-2-blue)](https://github.com/AlduinoCalderon/server-db-commands)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

## 📋 Table of Contents

- [Quick Start](#-quick-start)
  - [Graphical Interface (GUI)](#graphical-interface-gui)
  - [Console Application](#console-application)
- [Project Purpose](#-project-purpose)
- [Key Functionalities](#-key-functionalities)
- [GUI Features](#-gui-features)
- [Project Relevance](#-project-relevance)
- [Project Context](#-project-context)
- [Timeline](#-timeline)
- [Technologies](#-technologies)
- [Documentation](#-documentation)
- [Team](#-team)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)

## 🎯 Project Purpose

The **Server and Database Commands** project aims to automate the integration of research information for a university's Innovation Center. By leveraging the Google Scholar API, this system automatically retrieves, processes, and stores information about the institution's Top 3 researchers and their published work into the university's research database.

### Main Goal
Eliminate manual data entry processes and create an automated pipeline that:
- Retrieves researcher information from Google Scholar
- Processes and validates publication data
- Integrates seamlessly with existing database infrastructure
- Maintains accurate, up-to-date researcher profiles
- Provides both **graphical** and **console** interfaces for flexibility

## 🚀 Quick Start

### Complete Setup Guide (Windows PowerShell)

**Prerequisites:**
- Java JDK 11+ 
- Maven 3.6+
- MySQL 8.0+ (local or remote)
- SerpAPI account and key

**Step-by-step setup:**

1. **Clone the repository**
```powershell
git clone https://github.com/AlduinoCalderon/server-db-commands
cd server-db-commands
```

2. **Verify environment**
```powershell
mvn -v  # Should show Maven 3.6+ and Java 11+
```

3. **Build the project**
```powershell
# Complete build with JAR creation
mvn clean package -DskipTests

# Optional: Run tests to verify everything works
mvn test
```

4. **Configure environment variables**
```powershell
# Copy template to create .env file
Copy-Item .env.template .env

# Edit with your credentials
notepad .env
```

**Required variables in `.env`:**
```properties
# API Configuration (Get key from: https://serpapi.com/)
SERP_API_KEY=your_serpapi_key_here

# Database Configuration
DB_HOST=your_mysql_host
DB_PORT=3306
DB_NAME=your_database_name  
DB_USER=your_mysql_username
DB_PASSWORD=your_mysql_password
```

### Graphical Interface (GUI)

**Launch the JavaFX GUI** (Recommended for most users):

```powershell
mvn javafx:run
```

**GUI Features:**
- ✅ **Search Tab:** Query Google Scholar by researcher, keyword, or title
- ✅ **Browse Tab:** Filter and view database articles (All, By Author, By Year, Highly Cited)
- ✅ **Authors Tab:** Browse extracted authors with statistics and filtering
- ✅ **Statistics Tab:** View database metrics and analytics
- ✅ **CRUD Operations:** View details, soft delete, refresh tables
- ✅ **Real-time Progress:** Visual feedback for long-running operations
- ✅ **Modern UI:** Clean, color-coded interface with helpful hints

**GUI Architecture:**
```
ScholarGuiApplication
└── ScholarMainView (4 tabs)
    ├── Search Tab (API + Database search with progress bar)
    ├── Browse Tab (Database filtering with CRUD operations)
    ├── Authors Tab (Author management with statistics)
    └── Statistics Tab (Analytics and metrics)
```

**Table Columns:**
- **Articles:** ID, Title, Authors, Year, Journal, Citations
- **Authors:** ID, Full Name, Articles Count, Total Citations, Average Citations, First Seen

### Console Applications (Alternative to GUI)

**Option 1: Simple demo (recommended for testing)**
```powershell
mvn exec:java -Dexec.mainClass=com.innovationcenter.scholarapi.SimpleAutoSaveDemo
```

**Option 2: Full database test with API integration**
```powershell
mvn exec:java -Dexec.mainClass=com.innovationcenter.scholarapi.DatabaseTestRunner
```

**Option 3: Interactive console**
```powershell
mvn exec:java -Dexec.mainClass=com.innovationcenter.scholarapi.ScholarApiConsole
```

### Testing (Optional)

**Run unit tests:**
```powershell
mvn test
```

### Database Setup
- **Automatic:** The app will auto-create tables on first run
- **Manual:** See [DATABASE_README.md](docs/DATABASE_README.md) for complete schema

### What It Does
✅ **Automatically saves** articles to database  
✅ **Automatically retrieves** saved articles  
✅ **Extracts and tracks** author information  
✅ **Tests** complete MVC architecture  
✅ **Demonstrates** search and persistence workflow

## ⚙️ Key Functionalities

### 1. Automated Data Retrieval
- **Google Scholar Integration:** Connects to Google Scholar API to fetch researcher profiles
- **Publication Search:** Retrieves articles, citations, and publication metadata
- **Author-Specific Queries:** Filters results by researcher name
- **Pagination Handling:** Manages large result sets efficiently
- **Title Search:** Search for articles by title keywords

### 2. Data Processing & Validation
- **JSON Parsing:** Converts API responses into structured data
- **Field Mapping:** Maps API fields to database schema
- **Data Validation:** Ensures data quality before storage
- **Duplicate Detection:** Prevents redundant entries
- **Author Extraction:** Parses and normalizes author names from articles

### 3. Database Integration
- **Automated Storage:** Populates research database with retrieved information
- **Transaction Management:** Ensures data integrity
- **Error Handling:** Robust error recovery and logging
- **Update Mechanisms:** Handles updates to existing records
- **Soft Delete:** Articles and authors marked as deleted (not permanently removed)
- **Author Tracking:** Maintains author statistics (article count, citations, averages)

### 4. User Interfaces
- **Graphical Interface (JavaFX):** Modern, user-friendly GUI with tabs and CRUD operations
- **Console Interface:** Command-line interface for automated tasks
- **Real-time Feedback:** Progress bars and status updates
- **Data Visualization:** Tables, statistics, and analytics

### 5. Version Control & Documentation
- **GitHub Repository:** Complete version control
- **API Documentation:** Comprehensive technical documentation
- **Code Comments:** Well-documented codebase
- **Change Tracking:** Detailed commit history

## 🖥️ GUI Features

### 🔍 Search Tab
- **Search Types**:
  - **By Researcher Name:** Find all articles by a specific author
  - **By Query:** Search using keywords or phrases
  - **By Title (API):** Search Google Scholar by article title
- **Configurable Results:** Set maximum results (1-100 articles)
- **Real-time Progress:** Visual progress bar during searches
- **Auto-save:** Results automatically saved to database
- **Results Table:** Sortable columns with immediate viewing

### 📚 Browse Database Tab
- **Filter Options:**
  - **All Articles:** View entire database
  - **By Author:** Filter by author name (substring search)
  - **By Year (and newer):** Show articles from year X onwards
  - **Highly Cited:** Filter by minimum citation count
- **Helpful Hints:** Contextual tips for each filter type
- **CRUD Operations:**
  - 👁️ **View Details:** Popup with complete article information
  - 🗑️ **Delete:** Soft delete with confirmation dialog
  - 🔄 **Refresh:** Reload current view

### 👥 Authors Database Tab
- **Filter Options:**
  - **All Authors:** View all extracted authors
  - **Search by Name:** Find authors by name (substring search)
  - **Top by Citations:** Show most-cited authors (configurable limit)
  - **Top by Article Count:** Show most productive authors (configurable limit)
- **Author Statistics:** Total articles, citations, averages, timestamps
- **CRUD Operations:**
  - 📊 **View Author Stats:** Detailed statistics popup
  - 🗑️ **Delete Author:** Remove author (soft delete)
  - 🔄 **Refresh:** Reload current view

### 📊 Statistics Tab
- **Database Metrics:**
  - Total articles count
  - Total authors count
  - Database health status
- **Refresh Button:** Live updates

### UI Design
- **Modern Styling:** Dark blue header, color-coded buttons, custom CSS
- **Responsive Layout:** Adapts to window size
- **Keyboard Shortcuts:** Tab navigation, Enter to execute
- **Visual Feedback:** Status messages, confirmation dialogs
- **Sortable Tables:** Click column headers to sort

## 🌟 Project Relevance

### Problem Statement
The Innovation Center currently relies on **manual processes** to maintain researcher information in their database. This approach is:
- ⏰ **Time-consuming:** Hours spent on data entry
- ❌ **Error-prone:** High risk of human error
- 📊 **Outdated:** Information quickly becomes stale
- 📈 **Not scalable:** Cannot handle growing research output

### Solution Benefits

#### For the Institution
- **Efficiency Gains:** Reduces manual work from hours to minutes
- **Data Accuracy:** Eliminates human transcription errors
- **Real-time Updates:** Keeps researcher profiles current
- **Scalability:** Can easily expand to more researchers

#### For Researchers
- **Automated Profiles:** No need to manually update publication lists
- **Citation Tracking:** Automatic citation count updates
- **Visibility:** Better representation of research output

#### For Administration
- **Reporting:** Easy access to research metrics
- **Analytics:** Track research trends and impact
- **Compliance:** Maintain accurate records for accreditation

## 📖 Project Context

### Background Story

Renata, a talented project leader at the Innovation Center of a university in northern Mexico, faces a new challenge: automating the integration of information for the institution's **Top 3 researchers**. To accomplish this, she selects:
- **Elizabeth:** An outstanding programmer who will handle development
- **Sandra:** Database manager responsible for the research database

### Project Phases

#### Phase 1: Research & Documentation (Sprint 1)
Elizabeth reviews Google Scholar API documentation and analyzes current manual processes provided by Sandra. She organizes work to understand automation requirements.

**Deliverables:**
- ✅ GitHub repository setup
- ✅ Technical documentation of Google Scholar API
- ✅ Database mapping documentation

#### Phase 2: Java Development (Sprint 2)
Elizabeth develops Java code to extract researcher information using the MVC design pattern, storing data in memory and uploading to GitHub.

**Deliverables:**
- 🔄 Java classes implementing MVC pattern
- 🔄 GET request functionality
- 🔄 JSON parsing implementation
- 🔄 Unit tests

#### Phase 3: Database Integration (Sprint 3)
The project culminates with integration of the Java program that populates the research database, with complete documentation and version control.

**Deliverables:**
- ⏳ Database schema implementation
- ⏳ Data integration module
- ⏳ Pagination and error handling
- ⏳ Integration testing

### Project Outcome
The project successfully improves the institution's processes and strengthens the team's ability to face technological challenges through collaboration and technical excellence.

## 📅 Timeline

| Sprint | Dates | Focus | Status |
|--------|-------|-------|--------|
| **Sprint 1** | Sep 29-30, 2025 | API Research & Documentation | ✅ Completed |
| **Sprint 2** | Oct 1-2, 2025 | Java Development & GUI | ✅ Completed |
| **Sprint 3** | Oct 3-5, 2025 | Database Integration | ✅ Completed |
| **Final Delivery** | Oct 6, 2025 | Project Completion | 🔄 In Progress |

**Current Status:** Sprint 3 - GUI Testing & Final Integration

## 🛠 Technologies

### Core Technologies
- **Language:** Java (JDK 11+)
- **Design Pattern:** Model-View-Controller (MVC)
- **API:** Google Scholar API (via SerpAPI)
- **Database:** MySQL (with soft delete pattern)
- **GUI Framework:** JavaFX 17.0.8

### Development Tools
- **Version Control:** Git/GitHub
- **Build Tool:** Maven
- **Testing:** JUnit 5
- **IDE:** IntelliJ IDEA / Eclipse / VS Code

### Libraries & Dependencies
```xml
<!-- API Client -->
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20230227</version>
</dependency>

<!-- HTTP Client -->
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.11.0</version>
</dependency>

<!-- Database -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>5.0.1</version>
</dependency>

<!-- Configuration -->
<dependency>
    <groupId>io.github.cdimascio</groupId>
    <artifactId>dotenv-java</artifactId>
    <version>3.0.0</version>
</dependency>

<!-- JavaFX -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>17.0.8</version>
</dependency>

<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-fxml</artifactId>
    <version>17.0.8</version>
</dependency>

<!-- Testing -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.9.3</version>
    <scope>test</scope>
</dependency>
```

## 📚 Documentation

### Repository Structure
```
server-db-commands/
├── docs/
│   ├── Technical Report.md       # API technical documentation
│   ├── DATABASE_README.md        # Database schema and setup
│   ├── Roadmap.md                # Project roadmap
│   ├── Backlog.md                # User stories and backlog
│   ├── GanttChart.html           # Project timeline
│   └── GanttChart.png            # Visual timeline
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/innovationcenter/scholarapi/
│   │   │       ├── model/            # Data models (Article, SimpleAuthor)
│   │   │       ├── view/             # View layer (console + GUI)
│   │   │       ├── controller/       # Controllers
│   │   │       ├── service/          # Business logic services
│   │   │       ├── repository/       # Data access layer
│   │   │       ├── util/             # Utilities (AuthorParser)
│   │   │       ├── ScholarApiConsole.java      # Console entry point
│   │   │       └── ScholarGuiApplication.java  # GUI entry point
│   │   └── resources/
│   │       ├── styles/
│   │       │   └── application.css   # GUI styling
│   │       └── .env                  # Configuration (not in repo)
│   └── test/
│       └── java/                     # Unit tests
├── README.md                         # This file
├── .gitignore
└── pom.xml
```

### Key Documents
- **[Technical Report](docs/Technical%20Report.md):** Comprehensive Google Scholar API documentation
- **[Database Documentation](docs/DATABASE_README.md):** Schema, setup, and migration guides
- **[Project Roadmap](docs/Roadmap.md):** Objectives, timeline, and deliverables
- **[Project Backlog](docs/Backlog.md):** User stories and requirements
- **[Gantt Chart](docs/GanttChart.html):** Visual project timeline

## 👥 Team

### Project Roles

| Name | Role | Responsibilities |
|------|------|------------------|
| **Renata** | Project Leader | Project management, stakeholder communication |
| **Elizabeth** | Lead Developer | API integration, Java development |
| **Sandra** | Database Manager | Database design, data integration |

### Contact
- **Repository:** [AlduinoCalderon/server-db-commands](https://github.com/AlduinoCalderon/server-db-commands)
- **Pathway:** Backend Development
- **Institution:** Innovation Center, Northern Mexico University

## 🚀 Getting Started

### Prerequisites
```bash
- Java JDK 11 or higher
- Maven 3.6+
- Git
- SerpAPI account and API key
- MySQL 8.0+ (local or remote)
```

### Installation

1. **Clone the repository**
```powershell
git clone https://github.com/AlduinoCalderon/server-db-commands
cd server-db-commands
```

2. **Verify environment**
```powershell
mvn -v  # Should show Maven 3.6+ and Java 11+
```

3. **Compile the project**
```powershell
mvn clean compile -DskipTests
```

4. **Set up environment variables**

Copy the template and configure your credentials:
```powershell
# Copy the template to project root
Copy-Item .env.template .env

# Edit with your actual values
notepad .env
```

Required configuration in `.env` (note: SERP_API_KEY with underscore):
```properties
# API Configuration (Get key from: https://serpapi.com/)
SERP_API_KEY=your_serpapi_key_here

# Database Configuration
DB_HOST=localhost               # Your MySQL host
DB_PORT=3306                    # MySQL port (default: 3306)
DB_NAME=scholar_db              # Database name
DB_USER=your_database_user      # Database username
DB_PASSWORD=your_database_password  # Database password
```

**Note:** The `.env` file is gitignored for security. Never commit credentials!

5. **Run database setup** (first time only)

The application will automatically create tables on first run. Or manually:
```sql
-- See docs/DATABASE_README.md for complete schema
```

6. **Launch the application**

**GUI Version (Recommended):**
```powershell
mvn javafx:run
```

**Console Version:**
```powershell
mvn exec:java -Dexec.mainClass=com.innovationcenter.scholarapi.ScholarApiConsole
```

**Quick Demo:**
```powershell
mvn exec:java -Dexec.mainClass=com.innovationcenter.scholarapi.SimpleAutoSaveDemo
```

### Usage Examples

**Using the GUI:**
1. Launch: `mvn javafx:run`
2. Go to **Search** tab
3. Select search type (Researcher, Query, or Title)
4. Enter your search term
5. Set max results (1-100)
6. Click **Search** - results auto-save to database
7. Switch to **Browse** tab to filter and manage articles
8. Use **Authors** tab to explore extracted authors

**Using Console:**
```java
// Search for articles
ScholarApiConsole console = new ScholarApiConsole();
console.searchByAuthor("John Doe", 10);

// Browse database
console.displayArticles();
```

## 🎨 GUI Customization

### Styling
Edit `src/main/resources/styles/application.css` to customize:
- **Colors:** Button colors, table styles, header background
- **Fonts:** Text size, font family
- **Layout:** Spacing, padding, borders

### Keyboard Shortcuts
- **Tab:** Navigate between fields
- **Enter:** Execute search (when in search field)
- **Esc:** Clear filters/search
- **Ctrl+R:** Refresh current view

## 🐛 Troubleshooting

### GUI doesn't start
```powershell
# Ensure JavaFX is available
mvn clean compile
mvn javafx:run

# Check Java version
java -version  # Should be 11+
```

### Database connection error
- Verify `.env` file exists in project root (next to `pom.xml`)
- Check database credentials in `.env`
- Ensure MySQL server is running
- Test connection: `mysql -h <host> -u <user> -p`

### No search results
- Verify `SERP_API_KEY` in `.env` (note the underscore)
- Check internet connection
- Try different search terms
- Check API quota/limits at https://serpapi.com/

### Build errors
```powershell
# Clean and rebuild
mvn clean
mvn compile

# Update dependencies
mvn clean install -U
```

### Windows-specific issues
- If JavaFX fails to load, ensure you're using JDK 11+ (not JRE)
- For "module not found" errors, try: `mvn clean compile -DskipTests`
- If PowerShell commands fail, ensure execution policy allows scripts

## 📁 Project Structure

### MVC Architecture

```
Model (Data Layer)
├── Article.java              # Article entity with soft delete
├── SimpleAuthor.java         # Author entity with statistics
└── (Domain models)

View (Presentation Layer)
├── ConsoleView.java          # Console interface
├── ScholarGuiApplication.java # JavaFX GUI entry point
└── ScholarMainView.java      # Main GUI with 4 tabs

Controller (Business Logic)
├── ScholarController.java    # Main controller
└── (Service layer)

Service (Business Logic)
├── ArticleService.java                # Article operations
├── ScholarSearchService.java          # API search
├── DatabaseService.java               # Database connection
├── ConfigurationService.java          # Configuration management
└── (Implementation classes)

Repository (Data Access)
├── ArticleRepository.java             # Article persistence
├── SimpleAuthorRepository.java        # Author persistence
└── (MySQL implementations)

Utilities
└── AuthorParser.java                  # Parse and normalize author names
```

### Database Schema

```sql
-- Articles table with soft delete
CREATE TABLE articles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(500),
    authors TEXT,
    publication_year INT,
    journal VARCHAR(255),
    num_citations INT,
    url TEXT,
    snippet TEXT,
    scholar_article_id VARCHAR(100) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
);

-- Authors table with statistics
CREATE TABLE authors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(255) NOT NULL,
    total_articles INT DEFAULT 0,
    total_citations INT DEFAULT 0,
    first_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    UNIQUE KEY unique_author_name (full_name)
);

-- Article-Author junction table
CREATE TABLE article_authors (
    article_id BIGINT,
    author_id BIGINT,
    PRIMARY KEY (article_id, author_id),
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE
);
```

See [DATABASE_README.md](docs/DATABASE_README.md) for complete schema and setup.

## 🤝 Contributing

This is an academic project for the Innovation Center. Internal contributions follow:

1. Create a feature branch
2. Implement changes following MVC pattern
3. Write/update tests
4. Submit pull request for review
5. Update documentation

### Branching Strategy
- `main` - Production-ready code
- `develop` - Integration branch
- `feature/*` - New features
- `bugfix/*` - Bug fixes
- `sprint-*` - Sprint-specific work

## 📊 Project Metrics

### Sprint 1 Achievements ✅
- ✅ Google Scholar API documented (3 hours)
- ✅ Database mapping completed (2 hours)
- ✅ GitHub repository established (0.5 hours)
- ✅ Technical report finalized (3 hours)

### Sprint 2 Achievements ✅
- ✅ MVC structure implementation (4 hours)
- ✅ API client development (8 hours)
- ✅ JSON parsing and data models (4 hours)
- ✅ Error handling and logging (5 hours)
- ✅ Console interface complete (3 hours)
- ✅ Unit tests written (4 hours)

### Sprint 3 Achievements ✅
- ✅ Database integration with MySQL (6 hours)
- ✅ Soft delete pattern implementation (2 hours)
- ✅ Author extraction and tracking (4 hours)
- ✅ Article-Author relationship management (3 hours)
- ✅ JavaFX GUI development (12 hours)
  - Search tab with API integration
  - Browse tab with filtering
  - Authors tab with statistics
  - Statistics tab with metrics
- ✅ CRUD operations (View, Delete, Refresh) (4 hours)
- ✅ CSS styling and UX improvements (2 hours)

### Current Features
- 🎯 **2 User Interfaces:** GUI (JavaFX) + Console
- 🔍 **3 Search Types:** Researcher, Query, Title
- 📊 **4 GUI Tabs:** Search, Browse, Authors, Statistics
- 💾 **Database Integration:** MySQL with HikariCP connection pooling
- 👥 **Author Tracking:** Automatic extraction and statistics
- 🗑️ **Soft Delete:** Safe deletion with recovery option
- ✅ **CRUD Operations:** Complete Create, Read, Update, Delete

## 🚧 Future Enhancements

### Planned Features
- [ ] Export results to CSV/Excel
- [ ] Advanced filtering (date ranges, citation ranges)
- [ ] Chart visualizations for statistics
- [ ] Author collaboration network graph
- [ ] Dark mode theme toggle
- [ ] Search history and saved searches
- [ ] Bookmarks/favorites system
- [ ] Batch operations (bulk delete, bulk export)
- [ ] Email notifications for new publications
- [ ] Report generation (PDF, HTML)
- [ ] REST API for external integration
- [ ] Docker containerization

## 📄 License

This project is developed for academic purposes at the Innovation Center.

---

## 📌 Important Links

- [Technical Documentation](docs/Technical%20Report.md)
- [Database Schema](docs/DATABASE_README.md)
- [Project Roadmap](docs/Roadmap.md)
- [Sprint Backlog](docs/Backlog.md)
- [Gantt Chart](docs/GanttChart.html)
- [SerpAPI Documentation](https://serpapi.com/google-scholar-api)

## 📸 Screenshots

### GUI Interface
*Coming soon: Screenshots of all 4 tabs*

## 🎓 Academic Context

**Institution:** Innovation Center, Northern Mexico University  
**Course:** Backend Development Pathway  
**Project Type:** Academic Research Automation  
**Team Size:** 3 members (Project Leader, Developer, Database Manager)

---

**Last Updated:** October 3, 2025  
**Current Sprint:** Sprint 3 (GUI Testing & Integration)  
**Project Status:** � Active Development

---

<p align="center">
  Made with ☕ by the Innovation Center Team<br>
  <strong>Alduino Calderon</strong> • Elizabeth • Sandra • Renata
</p>