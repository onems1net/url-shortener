# URL Shortener

A simple URL Shortener API built with Spring Boot.

Features
- Shorten a long URL
- Redirect a short URL to the original
- Get info about a short URL (created time, redirect count)

Requirements
- Java 17
- Maven

Run locally

Start the application:

```bash
mvn spring-boot:run
```

Or build and run the jar:

```bash
mvn package
java -jar target/url-shortener-0.0.1-SNAPSHOT.jar
```

API

- POST /api/shorten  { "url": "https://example.com" }
  - Returns 201 with { code, shortUrl, originalUrl }
- GET /{code}
  - Redirects (302/302) to the original URL
- GET /api/info/{code}
  - Returns info about the short URL

Run tests

```bash
mvn test
```

Notes
- Uses an in-memory ConcurrentHashMap as the data store.
- Short codes are 6-character Base62 strings generated securely and non-sequentially.
- Validates URL format and returns HTTP 400 for invalid input and 404 for missing short codes.

