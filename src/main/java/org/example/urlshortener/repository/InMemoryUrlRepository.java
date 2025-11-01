package org.example.urlshortener.repository;

import org.example.urlshortener.model.UrlMapping;
import org.example.urlshortener.exception.NotFoundException;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUrlRepository {

    private final Map<String, UrlMapping> store = new ConcurrentHashMap<>();

    public UrlMapping save(UrlMapping mapping) {
        store.put(mapping.getCode(), mapping);
        return mapping;
    }

    public Optional<UrlMapping> findByCode(String code) {
        return Optional.ofNullable(store.get(code));
    }

    public boolean existsByCode(String code) {
        return store.containsKey(code);
    }

    public UrlMapping getOrThrow(String code) {
        return findByCode(code).orElseThrow(() -> new NotFoundException("Short URL not found"));
    }
}

