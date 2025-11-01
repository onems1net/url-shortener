package org.example.urlshortener.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.urlshortener.dto.ShortenRequest;
import org.example.urlshortener.dto.ShortenResponse;
import org.example.urlshortener.dto.UrlInfoResponse;
import org.example.urlshortener.model.UrlMapping;
import org.example.urlshortener.service.UrlShortenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * REST controller for the URL Shortener API.
 *
 * <p>Endpoints:
 * <ul>
 *     <li>POST /api/shorten - shorten a long URL</li>
 *     <li>GET /{code} - redirect to original URL</li>
 *     <li>GET /api/info/{code} - retrieve info about a short code</li>
 * </ul>
 *
 * The controller delegates core logic (validation, code generation, storage) to {@link UrlShortenerService}.
 * It replies with appropriate HTTP status codes and uses the global exception handler for 4xx errors.
 */
@Tag(name = "URL Shortener", description = "Create short URLs, redirect, and inspect info")
@RestController
public class UrlShortenerController {

    private static final Logger log = LoggerFactory.getLogger(UrlShortenerController.class);

    private final UrlShortenerService service;

    public UrlShortenerController(UrlShortenerService service) {
        this.service = service;
    }

    /**
     * Create a short URL for the provided original URL.
     */
    @Operation(summary = "Shorten a URL", description = "Creates a short code for a provided long URL.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Short URL created", content = @Content(schema = @Schema(implementation = ShortenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid URL or request payload", content = @Content)
    })
    @PostMapping(value = "/api/shorten", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShortenResponse> shorten(@Valid @RequestBody ShortenRequest request) {
        String baseUri = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        ShortenResponse response = service.shorten(request.getUrl(), baseUri);
        log.info("Created short URL {} for original {}", response.getShortUrl(), response.getOriginalUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Redirect to the original URL for the given short code.
     */
    @Operation(summary = "Redirect to original", description = "Redirects the client to the original URL for the given short code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirect to original URL"),
            @ApiResponse(responseCode = "404", description = "Short code not found", content = @Content)
    })
    @GetMapping(path = "/{code:[0-9A-Za-z]{6}}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        log.debug("Redirect requested for code={}", code);
        String original = service.getOriginalUrlAndIncrement(code);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, original);
        log.info("Redirecting code {} -> {}", code, original);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    /**
     * Return metadata for a short code.
     */
    @Operation(summary = "Get short URL info", description = "Returns metadata (original URL, creation time, redirect count) for a short code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metadata returned", content = @Content(schema = @Schema(implementation = UrlInfoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Short code not found", content = @Content)
    })
    @GetMapping(path = "/api/info/{code:[0-9A-Za-z]{6}}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UrlInfoResponse> info(@PathVariable String code) {
        log.debug("Info requested for code={}", code);
        UrlMapping mapping = service.getInfo(code);
        UrlInfoResponse response = new UrlInfoResponse(mapping.getCode(), mapping.getOriginalUrl(), mapping.getCreatedAt(), mapping.getRedirectCount());
        return ResponseEntity.ok(response);
    }
}
