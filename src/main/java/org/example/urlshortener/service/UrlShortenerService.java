package org.example.urlshortener.service;

import org.example.urlshortener.dto.ShortenResponse;
import org.example.urlshortener.exception.NotFoundException;
import org.example.urlshortener.model.UrlMapping;
import org.example.urlshortener.repository.InMemoryUrlRepository;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.Locale;

@Service
public class UrlShortenerService {

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    private final InMemoryUrlRepository repository;

    public UrlShortenerService(InMemoryUrlRepository repository) {
        this.repository = repository;
    }

    public ShortenResponse shorten(String originalUrl, String baseUri) {
        validateUrl(originalUrl);

        // generate unique code
        String code = generateUniqueCode();
        UrlMapping mapping = new UrlMapping(code, originalUrl);
        repository.save(mapping);

        String shortUrl = buildShortUrl(baseUri, code);
        return new ShortenResponse(code, shortUrl, originalUrl);
    }

    private String buildShortUrl(String baseUri, String code) {
        if (baseUri.endsWith("/")) {
            return baseUri + code;
        }
        return baseUri + "/" + code;
    }

    private String generateUniqueCode() {
        int tries = 0;
        while (tries < 10) {
            String code = randomCode();
            if (!repository.existsByCode(code)) {
                return code;
            }
            tries++;
        }
        // fallback: try until unique (extremely unlikely to loop long)
        while (true) {
            String code = randomCode();
            if (!repository.existsByCode(code)) {
                return code;
            }
        }
    }

    private String randomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    private void validateUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
                throw new IllegalArgumentException("URL must have http or https scheme");
            }
            if (uri.getHost() == null) {
                throw new IllegalArgumentException("URL must have a valid host");
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL format");
        }
    }

    public String getOriginalUrlAndIncrement(String code) {
        UrlMapping mapping = repository.getOrThrow(code);
        synchronized (mapping) {
            mapping.incrementRedirectCount();
        }
        return mapping.getOriginalUrl();
    }

    public UrlMapping getInfo(String code) {
        return repository.getOrThrow(code);
    }
}

