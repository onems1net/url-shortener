package org.example.urlshortener.model;

import java.time.Instant;

public class UrlMapping {

    private final String code;
    private final String originalUrl;
    private final Instant createdAt;
    private int redirectCount;

    public UrlMapping(String code, String originalUrl) {
        this.code = code;
        this.originalUrl = originalUrl;
        this.createdAt = Instant.now();
        this.redirectCount = 0;
    }

    public String getCode() {
        return code;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public synchronized int getRedirectCount() {
        return redirectCount;
    }

    public synchronized void incrementRedirectCount() {
        this.redirectCount++;
    }
}

