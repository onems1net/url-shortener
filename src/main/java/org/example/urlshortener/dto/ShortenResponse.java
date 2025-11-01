package org.example.urlshortener.dto;

public class ShortenResponse {
    private String code;
    private String shortUrl;
    private String originalUrl;

    public ShortenResponse() {
    }

    public ShortenResponse(String code, String shortUrl, String originalUrl) {
        this.code = code;
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
}

