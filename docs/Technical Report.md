# Technical Report: Google Scholar API (SerpAPI)

**Project:** Server and Database Commands - Researcher Information Integration  
**Author:** Aldo Calderon (NAO ID: 3355)  
**Date:** October 1, 2025  
**Sprint:** Sprint 1 - API Research & Documentation

---

## Executive Summary

This technical report provides comprehensive documentation of the Google Scholar API (via SerpAPI) for integrating researcher and publication data into the university's research database. The API enables automated retrieval of scholarly information, citations, and publication metadata.

---

## 1. API Endpoints

### Primary Endpoint
```
Base URL: https://serpapi.com/search
Engine: google_scholar
```

### Main Search Endpoint
```
GET https://serpapi.com/search.json?engine=google_scholar
```

### Additional Endpoints
- **Citation Search:** `https://serpapi.com/search.json?engine=google_scholar_cite`
- **Author Profile:** Query with `author:` parameter
- **Cited By:** Query with `cites` parameter

---

## 2. Authentication Methods

### API Key Authentication
The Google Scholar API (SerpAPI) uses API key-based authentication:

**Method:** Query Parameter  
**Parameter Name:** `api_key`  
**Type:** String (64-character hexadecimal)

#### Example Authentication:
```ruby
client = SerpApi::Client.new(
  engine: "google_scholar",
  q: "biology",
  api_key: "YOUR_API_KEY_HERE"
)
```

#### Security Best Practices:
- Store API keys in environment variables
- Never commit API keys to version control
- Use `.gitignore` to exclude configuration files
- Rotate keys periodically

---

## 3. Query Parameters

### Required Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `engine` | String | Must be set to `"google_scholar"` |
| `api_key` | String | Your SerpAPI authentication key |

### Search Query Parameters

| Parameter | Type | Description | Example |
|-----------|------|-------------|---------|
| `q` | String | Search query (optional with `cites`) | `"machine learning"` |
| `author:` | Helper | Filter by author name | `author:"John Smith"` |
| `source:` | Helper | Filter by publication source | `source:"Nature"` |

### Advanced Search Parameters

| Parameter | Type | Description | Default |
|-----------|------|-------------|---------|
| `cites` | String | Unique ID to trigger "Cited By" searches | - |
| `cluster` | String | Unique ID to trigger "All Versions" searches | - |
| `as_ylo` | Integer | Year from which to include results | - |
| `as_yhi` | Integer | Year until which to include results | - |
| `scisbd` | Integer | Sort by date (0=relevance, 1=abstracts, 2=everything) | 0 |

### Pagination Parameters

| Parameter | Type | Description | Default | Range |
|-----------|------|-------------|---------|-------|
| `start` | Integer | Result offset for pagination | 0 | 0, 10, 20... |
| `num` | Integer | Maximum results per page | 10 | 1-20 |

### Localization Parameters

| Parameter | Type | Description | Example |
|-----------|------|-------------|---------|
| `hl` | String | Two-letter language code | `"en"`, `"es"` |
| `lr` | String | Limit search to specific languages | `"lang_en\|lang_es"` |

### Filter Parameters

| Parameter | Type | Description | Values |
|-----------|------|-------------|--------|
| `as_sdt` | Integer | Search type or filter | 0 (exclude patents), 7 (include patents), 4 (case law) |
| `safe` | String | Adult content filtering | `"active"`, `"off"` |
| `filter` | Integer | Enable similar/omitted results filter | 0 (disabled), 1 (enabled) |
| `as_vis` | Integer | Include/exclude citations | 0 (include), 1 (exclude) |
| `as_rr` | Integer | Show only review articles | 0 (all), 1 (reviews only) |

---

## 4. Response Formats

### JSON Response Structure

The API returns structured JSON data with the following main sections:

#### Search Metadata
```json
{
  "search_metadata": {
    "id": "unique_search_id",
    "status": "Success",
    "created_at": "2025-10-01 04:40:57 UTC",
    "processed_at": "2025-10-01 04:40:57 UTC",
    "total_time_taken": 1.24
  }
}
```

#### Search Parameters
```json
{
  "search_parameters": {
    "engine": "google_scholar",
    "q": "researcher query"
  }
}
```

#### Search Information
```json
{
  "search_information": {
    "total_results": 5880000,
    "time_taken_displayed": 0.06,
    "query_displayed": "biology"
  }
}
```

#### Organic Results (Main Data)
```json
{
  "organic_results": [
    {
      "position": 0,
      "title": "Article Title",
      "result_id": "unique_result_id",
      "link": "https://article-url.com",
      "snippet": "Article summary or abstract",
      "publication_info": {
        "summary": "Authors - Journal, Year - Publisher"
      },
      "inline_links": {
        "cited_by": {
          "total": 14003,
          "link": "citation_url",
          "cites_id": "citation_id"
        },
        "versions": {
          "total": 6,
          "cluster_id": "cluster_id"
        }
      },
      "resources": [
        {
          "title": "PDF Source",
          "file_format": "PDF",
          "link": "pdf_url"
        }
      ]
    }
  ]
}
```

#### Related Searches
```json
{
  "related_searches": [
    {
      "query": "molecular biology",
      "link": "search_url"
    }
  ]
}
```

#### Pagination
```json
{
  "pagination": {
    "current": 1,
    "next": "next_page_url",
    "other_pages": {
      "2": "page_2_url",
      "3": "page_3_url"
    }
  }
}
```

### Alternative Response Formats

| Format | Parameter | Use Case |
|--------|-----------|----------|
| JSON | `output=json` | Structured data processing (default) |
| HTML | `output=html` | Debugging or unsupported features |

---

## 5. Usage Limits

### Rate Limiting

**SerpAPI Rate Limits:**
- Depends on subscription plan
- Free tier: Limited to 250 searches per month
- Paid plans: Higher search quotas
- Rate limit headers included in responses

### Best Practices for Rate Limiting:
1. **Implement Exponential Backoff:** Wait progressively longer between retries
2. **Cache Results:** Store responses for 1 hour (SerpAPI cache duration)
3. **Batch Requests:** Group queries where possible
4. **Monitor Usage:** Track API calls against quota

### Request Optimization:
```ruby
# Use caching to avoid duplicate requests
client = SerpApi::Client.new(
  engine: "google_scholar",
  q: "researcher name",
  api_key: ENV['SERPAPI_KEY'],
  no_cache: false  # Allow cached results
)
```

### Google Scholar Limits:
- Google Scholar may temporarily block excessive requests
- Implement delays between requests (recommended: 2-5 seconds)
- Use pagination efficiently with `start` parameter

---

## 6. Code Examples

### Ruby Implementation

#### Basic Search
```ruby
require "serpapi"

# Initialize client
client = SerpApi::Client.new(
  engine: "google_scholar",
  q: "machine learning",
  api_key: ENV['SERPAPI_KEY']
)

# Execute search
results = client.search
organic_results = results[:organic_results]

# Process results
organic_results.each do |result|
  puts "Title: #{result[:title]}"
  puts "Authors: #{result[:publication_info][:summary]}"
  puts "Citations: #{result[:inline_links][:cited_by][:total]}" if result[:inline_links][:cited_by]
  puts "---"
end
```

#### Author-Specific Search
```ruby
client = SerpApi::Client.new(
  engine: "google_scholar",
  q: 'author:"John Smith"',
  api_key: ENV['SERPAPI_KEY']
)

results = client.search
```

#### Cited By Search
```ruby
# Get articles citing a specific paper
client = SerpApi::Client.new(
  engine: "google_scholar",
  cites: "1275980731835430123",
  api_key: ENV['SERPAPI_KEY']
)

citing_articles = client.search[:organic_results]
```

#### Pagination Implementation
```ruby
def fetch_all_results(query, max_pages = 5)
  all_results = []
  
  (0...max_pages).each do |page|
    client = SerpApi::Client.new(
      engine: "google_scholar",
      q: query,
      start: page * 10,
      api_key: ENV['SERPAPI_KEY']
    )
    
    results = client.search
    break if results[:organic_results].nil? || results[:organic_results].empty?
    
    all_results.concat(results[:organic_results])
    sleep(2) # Rate limiting delay
  end
  
  all_results
end
```

### Java Implementation (for Sprint 2)

#### Basic Structure
```java
import org.json.JSONObject;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class GoogleScholarClient {
    private static final String BASE_URL = "https://serpapi.com/search.json";
    private String apiKey;
    
    public GoogleScholarClient(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public JSONObject search(String query) throws Exception {
        String url = String.format(
            "%s?engine=google_scholar&q=%s&api_key=%s",
            BASE_URL,
            URLEncoder.encode(query, StandardCharsets.UTF_8),
            apiKey
        );
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
            
        HttpResponse<String> response = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );
        
        return new JSONObject(response.body());
    }
}
```

### Python Implementation
```python
from serpapi import GoogleSearch

# Basic search
params = {
    "engine": "google_scholar",
    "q": "artificial intelligence",
    "api_key": "YOUR_API_KEY"
}

search = GoogleSearch(params)
results = search.get_dict()
organic_results = results.get("organic_results", [])

for result in organic_results:
    print(f"Title: {result.get('title')}")
    print(f"Snippet: {result.get('snippet')}")
    if 'cited_by' in result.get('inline_links', {}):
        print(f"Citations: {result['inline_links']['cited_by']['total']}")
    print("---")
```

---

## 7. Error Handling

### Common HTTP Status Codes

| Code | Status | Description | Action |
|------|--------|-------------|--------|
| 200 | Success | Request successful | Process results |
| 400 | Bad Request | Invalid parameters | Check query syntax |
| 401 | Unauthorized | Invalid API key | Verify authentication |
| 429 | Too Many Requests | Rate limit exceeded | Implement backoff |
| 500 | Server Error | SerpAPI internal error | Retry after delay |

### Error Response Structure
```json
{
  "error": "Invalid API key",
  "search_metadata": {
    "status": "Error"
  }
}
```

### Error Handling Example
```ruby
begin
  results = client.search
  
  if results[:search_metadata][:status] == "Error"
    raise StandardError, results[:error]
  end
  
  # Process results
  organic_results = results[:organic_results]
  
rescue StandardError => e
  puts "Error: #{e.message}"
  # Log error and implement retry logic
end
```

---

## 8. Database Mapping

### Researcher Data Model

| API Field | Database Column | Type | Notes |
|-----------|----------------|------|-------|
| `title` | `paper_title` | VARCHAR(500) | Article title |
| `publication_info.summary` | `authors` | TEXT | Comma-separated authors |
| `publication_info.summary` | `publication_year` | INTEGER | Extracted from summary |
| `publication_info.summary` | `journal` | VARCHAR(255) | Publication source |
| `link` | `article_url` | VARCHAR(500) | Primary URL |
| `snippet` | `abstract` | TEXT | Article abstract |
| `result_id` | `google_scholar_id` | VARCHAR(50) | Unique identifier |
| `inline_links.cited_by.total` | `citation_count` | INTEGER | Number of citations |
| `inline_links.cited_by.cites_id` | `cites_id` | VARCHAR(50) | For citation tracking |
| `resources[0].link` | `pdf_url` | VARCHAR(500) | Direct PDF link |

### Parsing Publication Info
The `publication_info.summary` field contains combined data:
```
Format: "Author1, Author2 - Source, Year - Publisher"
Example: "JL Harper - Population biology of plants., 1977 - cabdirect.org"
```

**Parsing Strategy:**
```ruby
def parse_publication_info(summary)
  parts = summary.split(' - ')
  {
    authors: parts[0],
    source_year: parts[1],
    publisher: parts[2]
  }
end
```

---

## 9. Implementation Recommendations

### Phase 1: Data Retrieval (Sprint 2)
1. Implement basic search functionality
2. Add author-specific queries
3. Implement pagination handling
4. Add error handling and logging

### Phase 2: Data Processing (Sprint 2)
1. Parse publication_info field
2. Extract year from publication data
3. Handle missing fields gracefully
4. Validate data before storage

### Phase 3: Database Integration (Sprint 3)
1. Create database schema
2. Implement data mapping
3. Handle duplicate detection
4. Implement transaction management

### Caching Strategy
- Cache API responses for 1 hour
- Store raw JSON for debugging
- Implement cache invalidation
- Use Redis or file-based cache

### Monitoring and Logging
- Log all API requests
- Track response times
- Monitor error rates
- Alert on quota approaching

---

## 10. Security Considerations

### API Key Security
- ✅ Store in environment variables
- ✅ Use `.env` files (not committed)
- ✅ Implement key rotation
- ✅ Restrict key permissions

### Data Privacy
- Comply with data protection regulations
- Implement access controls
- Audit data access logs
- Encrypt sensitive data at rest

### Rate Limiting Protection
- Implement request throttling
- Use circuit breaker pattern
- Queue requests during high load
- Monitor usage patterns

---

## 11. Testing Strategy

### Unit Tests
```ruby
RSpec.describe GoogleScholarClient do
  it "successfully retrieves search results" do
    client = GoogleScholarClient.new(ENV['SERPAPI_KEY'])
    results = client.search("test query")
    
    expect(results[:search_metadata][:status]).to eq("Success")
    expect(results[:organic_results]).not_to be_empty
  end
end
```

### Integration Tests
- Test pagination across multiple pages
- Verify data mapping accuracy
- Test error handling scenarios
- Validate rate limiting compliance

---

## 12. References

- **SerpAPI Documentation:** https://serpapi.com/google-scholar-api
- **Google Scholar:** https://scholar.google.com
- **Ruby SerpAPI Client:** https://github.com/serpapi/google-search-results-ruby
- **JSON Schema Validation:** https://json-schema.org/

---


## Appendix A: Glossary

| Term | Definition |
|------|------------|
| **Organic Results** | Main search results containing publications |
| **Cited By** | Number of articles citing the publication |
| **Cluster ID** | Identifier for grouping article versions |
| **Result ID** | Unique identifier for a publication |
| **SERP** | Search Engine Results Page |

---

**Document Version:** 1.0  
**Last Updated:** September 30, 2025  
**Next Review:** Sprint 2 Review (October 2, 2025)