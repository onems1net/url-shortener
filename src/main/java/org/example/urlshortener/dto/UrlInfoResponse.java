package org.example.urlshortener.dto;

import java.time.Instant;

public class UrlInfoResponse {
    private String code;
    private String originalUrl;
    private Instant createdAt;
    private int redirectCount;

    public UrlInfoResponse() {
    }

    public UrlInfoResponse(String code, String originalUrl, Instant createdAt, int redirectCount) {
        this.code = code;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
        this.redirectCount = redirectCount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public int getRedirectCount() {
        return redirectCount;
    }

    public void setRedirectCount(int redirectCount) {
        this.redirectCount = redirectCount;
    }
}

