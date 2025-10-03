# Database Schema - Scholar API System

## Current Active Schema

### **1. Articles Table** (Currently in use)
```sql
CREATE TABLE IF NOT EXISTS articles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    paper_title VARCHAR(500) NOT NULL,
    authors VARCHAR(1000),
    publication_year INTEGER,
    journal VARCHAR(500),
    article_url VARCHAR(500),
    abstract_text TEXT,
    google_scholar_id VARCHAR(50) UNIQUE,
    citation_count INTEGER DEFAULT 0,
    cites_id VARCHAR(50),
    pdf_url VARCHAR(500),
    publisher VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    INDEX idx_google_scholar_id (google_scholar_id),
    INDEX idx_publication_year (publication_year),
    INDEX idx_citation_count (citation_count),
    INDEX idx_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### **2. Authors Table** (Author extraction)
```sql
CREATE TABLE IF NOT EXISTS authors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(255) NOT NULL,
    first_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    article_count INT DEFAULT 0,
    total_citations INT DEFAULT 0,
    UNIQUE INDEX idx_full_name (full_name),
    INDEX idx_article_count (article_count),
    INDEX idx_total_citations (total_citations),
    INDEX idx_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### **3. Article-Author Relationship Table**
```sql
CREATE TABLE IF NOT EXISTS article_authors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    article_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    author_position INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE,
    UNIQUE INDEX idx_article_author (article_id, author_id),
    INDEX idx_author_id (author_id),
    INDEX idx_article_id (article_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## Logical Delete (Soft Delete)

Both `articles` and `authors` tables use **soft delete** via the `deleted_at` column:
- When `deleted_at IS NULL` ↁErecord is active
- When `deleted_at` has a timestamp ↁErecord is deleted (but preserved)

### Soft Delete Examples:
```sql
-- Soft delete an article
UPDATE articles SET deleted_at = CURRENT_TIMESTAMP WHERE id = 1;

-- Soft delete an author  
UPDATE authors SET deleted_at = CURRENT_TIMESTAMP WHERE id = 1;

-- Restore a deleted article
UPDATE articles SET deleted_at = NULL WHERE id = 1;

-- Query only active articles
SELECT * FROM articles WHERE deleted_at IS NULL;

-- Query only deleted articles
SELECT * FROM articles WHERE deleted_at IS NOT NULL;
```

---

## Useful Queries

### Get all authors for an article
```sql
SELECT 
    a.paper_title,
    au.full_name,
    aa.author_position
FROM articles a
JOIN article_authors aa ON a.id = aa.article_id
JOIN authors au ON aa.author_id = au.id
WHERE a.id = ? AND a.deleted_at IS NULL AND au.deleted_at IS NULL
ORDER BY aa.author_position;
```

### Get all articles by an author
```sql
SELECT 
    a.*,
    aa.author_position
FROM articles a
JOIN article_authors aa ON a.id = aa.article_id
JOIN authors au ON aa.author_id = au.id
WHERE au.full_name = ? AND a.deleted_at IS NULL AND au.deleted_at IS NULL
ORDER BY a.publication_year DESC;
```

### Top authors by citations
```sql
SELECT 
    full_name,
    article_count,
    total_citations,
    ROUND(total_citations / article_count, 2) as avg_citations
FROM authors
WHERE deleted_at IS NULL AND article_count > 0
ORDER BY total_citations DESC
LIMIT 20;
```

### Find co-authors
```sql
SELECT 
    a1.full_name as author1,
    a2.full_name as author2,
    COUNT(*) as papers_together
FROM article_authors aa1
JOIN article_authors aa2 ON aa1.article_id = aa2.article_id AND aa1.author_id < aa2.author_id
JOIN authors a1 ON aa1.author_id = a1.id
JOIN authors a2 ON aa2.author_id = a2.id
WHERE a1.deleted_at IS NULL AND a2.deleted_at IS NULL
GROUP BY a1.id, a2.id
