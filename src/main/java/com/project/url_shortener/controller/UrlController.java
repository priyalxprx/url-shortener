package com.project.url_shortener.controller;

import com.project.url_shortener.dto.ShortenRequest;
import com.project.url_shortener.dto.ShortenResponse;
import com.project.url_shortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/urls")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shorten(
            @Valid @RequestBody ShortenRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(urlService.shorten(request, email));
    }

    @GetMapping("/my-links")
    public ResponseEntity<List<ShortenResponse>> getMyLinks(
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(urlService.getMyUrls(email));
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<String> deleteUrl(
            @PathVariable String shortCode,
            Authentication authentication) {
        String email = authentication.getName();
        urlService.deleteUrl(shortCode, email);
        return ResponseEntity.ok("URL deleted successfully");
    }
}