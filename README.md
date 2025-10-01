# Server and Database Commands

> Automating Researcher Information Integration with Google Scholar API

[![Project Status](https://img.shields.io/badge/status-in%20development-yellow)](https://github.com/AlduinoCalderon/server-db-commands)
[![Sprint](https://img.shields.io/badge/sprint-1-blue)](https://github.com/AlduinoCalderon/server-db-commands)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

## 📋 Table of Contents

- [Project Purpose](#-project-purpose)
- [Key Functionalities](#-key-functionalities)
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

The **Server and Database Commands** project aims to automate the integration of research information for a university's Innovation Center. By leveraging the Google Scholar API, this system will automatically retrieve, process, and store information about the institution's Top 3 researchers and their published work into the university's research database.

### Main Goal
Eliminate manual data entry processes and create an automated pipeline that:
- Retrieves researcher information from Google Scholar
- Processes and validates publication data
- Integrates seamlessly with existing database infrastructure
- Maintains accurate, up-to-date researcher profiles

## ⚙️ Key Functionalities

### 1. Automated Data Retrieval
- **Google Scholar Integration:** Connects to Google Scholar API to fetch researcher profiles
- **Publication Search:** Retrieves articles, citations, and publication metadata
- **Author-Specific Queries:** Filters results by researcher name
- **Pagination Handling:** Manages large result sets efficiently

### 2. Data Processing & Validation
- **JSON Parsing:** Converts API responses into structured data
- **Field Mapping:** Maps API fields to database schema
- **Data Validation:** Ensures data quality before storage
- **Duplicate Detection:** Prevents redundant entries

### 3. Database Integration
- **Automated Storage:** Populates research database with retrieved information
- **Transaction Management:** Ensures data integrity
- **Error Handling:** Robust error recovery and logging
- **Update Mechanisms:** Handles updates to existing records

### 4. Version Control & Documentation
- **GitHub Repository:** Complete version control
- **API Documentation:** Comprehensive technical documentation
- **Code Comments:** Well-documented codebase
- **Change Tracking:** Detailed commit history

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
| **Sprint 2** | Oct 1-2, 2025 | Java Development | 🔄 In Progress |
| **Sprint 3** | Oct 3-5, 2025 | Database Integration | ⏳ Pending |
| **Final Delivery** | Oct 6, 2025 | Project Completion | ⏳ Pending |

**Current Status:** Sprint 2 - API Development (Day 1)

## 🛠 Technologies

### Core Technologies
- **Language:** Java (JDK 11+)
- **Design Pattern:** Model-View-Controller (MVC)
- **API:** Google Scholar API (via SerpAPI)
- **Database:** SQL (PostgreSQL/MySQL)

### Development Tools
- **Version Control:** Git/GitHub
- **Build Tool:** Maven/Gradle
- **Testing:** JUnit 5
- **IDE:** IntelliJ IDEA / Eclipse

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
│   ├── TECHNICAL_REPORT.md       # API technical documentation
│   ├── roadmap.md                # Project roadmap
│   ├── backlog.md                # User stories and backlog
│   └── gantt_chart.html          # Project timeline
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── model/            # Data models
│   │       ├── view/             # View components
│   │       └── controller/       # Business logic
│   └── test/
│       └── java/                 # Unit tests
├── README.md
├── .gitignore
└── pom.xml
```

### Key Documents
- **[Technical Report](docs/TECHNICAL_REPORT.md):** Comprehensive Google Scholar API documentation
- **[Project Roadmap](docs/roadmap.md):** Objectives, timeline, and deliverables
- **[Project Backlog](docs/backlog.md):** User stories and requirements
- **[Gantt Chart](docs/gantt_chart.html):** Visual project timeline

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
```

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/AlduinoCalderon/server-db-commands.git
cd server-db-commands
```

2. **Set up environment variables**
```bash
# Create .env file
echo "SERPAPI_KEY=your_api_key_here" > .env
```

3. **Build the project**
```bash
mvn clean install
```

4. **Run tests**
```bash
mvn test
```

### Quick Start Example
```java
// Coming in Sprint 2
// Example usage will be added once API client is implemented
```

## 📁 Project Structure

### MVC Architecture

```
Model (Data Layer)
├── Researcher.java
├── Publication.java
└── Citation.java

View (Presentation Layer)
├── ConsoleView.java
└── ReportView.java

Controller (Business Logic)
├── ScholarAPIController.java
├── DatabaseController.java
└── ValidationController.java
```

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

### Sprint 1 Achievements
- ✅ Google Scholar API documented (3 hours)
- ✅ Database mapping completed (2 hours)
- ✅ GitHub repository established (0.5 hours)
- ✅ Technical report finalized (3 hours)

### Sprint 2 Goals
- 🔄 MVC structure implementation (4 hours)
- 🔄 API client development (8 hours)
- 🔄 JSON parsing (4 hours)
- 🔄 Error handling (5 hours)

## 📄 License

This project is developed for academic purposes at the Innovation Center.

---

## 📌 Important Links

- [API Technical Documentation](docs/TECHNICAL_REPORT.md)
- [Project Roadmap](docs/roadmap.md)
- [Sprint Backlog](docs/backlog.md)
- [Gantt Chart](docs/gantt_chart.html)
- [SerpAPI Documentation](https://serpapi.com/google-scholar-api)

---

**Last Updated:** September 30, 2025  
**Current Sprint:** Sprint 1 
**Project Status:** 🟡 In Development

---

<p align="center">
  Made with ☕ by the Alduino Calderon
</p>