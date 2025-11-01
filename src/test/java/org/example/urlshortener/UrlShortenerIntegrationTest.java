package org.example.urlshortener;

import org.example.urlshortener.dto.ShortenRequest;
import org.example.urlshortener.dto.ShortenResponse;
import org.example.urlshortener.dto.UrlInfoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UrlShortenerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shortenAndRedirectAndInfo() {
        String original = "https://www.originenergy.com.au/electricity-gas/plans.html";
        ShortenRequest req = new ShortenRequest(original);
        ResponseEntity<ShortenResponse> post = restTemplate.postForEntity("/api/shorten", req, ShortenResponse.class);
        assertThat(post.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ShortenResponse body = post.getBody();
        assertThat(body).isNotNull();

        // basic response shape
        assertThat(body.getShortUrl()).contains("/");

        // additional assertions: code format and shortUrl origin
        assertThat(body.getCode()).matches("[0-9A-Za-z]{6}");
        assertThat(body.getCode().length()).isEqualTo(6);
        assertThat(body.getShortUrl()).startsWith("http://localhost:" + port + "/");

        // follow redirect (do not automatically follow)
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> redirectResp = restTemplate.exchange(body.getShortUrl(), HttpMethod.GET, entity, String.class);
        assertThat(redirectResp.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        // ensure Location header exists before converting to string
        assertThat(redirectResp.getHeaders().getLocation()).isNotNull();
        assertThat(redirectResp.getHeaders().getLocation().toString()).isEqualTo(original);

        // info
        String code = body.getCode();
        ResponseEntity<UrlInfoResponse> infoResp = restTemplate.getForEntity("/api/info/" + code, UrlInfoResponse.class);
        assertThat(infoResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        UrlInfoResponse info = infoResp.getBody();
        assertThat(info).isNotNull();

        // validate returned info fields
        assertThat(info.getCode()).isEqualTo(code);
        assertThat(info.getOriginalUrl()).isEqualTo(original);
        assertThat(info.getCreatedAt()).isNotNull();
        // we performed a single redirect above, so expect exactly 1
        assertThat(info.getRedirectCount()).isEqualTo(1);

        // Shortening the same original again should produce a new short code (service currently does not dedupe)
        ResponseEntity<ShortenResponse> post2 = restTemplate.postForEntity("/api/shorten", req, ShortenResponse.class);
        assertThat(post2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ShortenResponse body2 = post2.getBody();
        assertThat(body2).isNotNull();
        assertThat(body2.getCode()).isNotEqualTo(code);
        assertThat(body2.getShortUrl()).isNotEqualTo(body.getShortUrl());

        // requesting info for a non-existent code returns 404
        ResponseEntity<String> missing = restTemplate.getForEntity("/api/info/unknown123", String.class);
        assertThat(missing.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void invalidUrlReturnsBadRequest() {
        ShortenRequest req = new ShortenRequest("ht!tp://bad-url");
        ResponseEntity<String> post = restTemplate.postForEntity("/api/shorten", req, String.class);
        assertThat(post.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
