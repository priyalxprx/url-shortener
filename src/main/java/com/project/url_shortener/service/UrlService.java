package com.project.url_shortener.service;

import com.project.url_shortener.dto.ShortenRequest;
import com.project.url_shortener.dto.ShortenResponse;
import com.project.url_shortener.model.Url;
import com.project.url_shortener.model.User;
import com.project.url_shortener.repository.UrlRepository;
import com.project.url_shortener.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UrlService {

    private static final String BASE62 =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String BASE_URL = "http://localhost:8080/";

    private final UrlRepository urlRepository;
    private final UserRepository userRepository;
    private final RedisService redisService;

    public UrlService(UrlRepository urlRepository,
                      UserRepository userRepository,
                      RedisService redisService) {
        this.urlRepository = urlRepository;
        this.userRepository = userRepository;
        this.redisService = redisService;
    }

    public ShortenResponse shorten(ShortenRequest request, String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        String code = generateUniqueCode();

        Url url = new Url();
        url.setShortCode(code);
        url.setOriginalUrl(request.getOriginalUrl());
        url.setUserId(user.getId());
        urlRepository.save(url);

        redisService.save(code, request.getOriginalUrl(), 30);

        return new ShortenResponse(
            code,
            BASE_URL + code,
            request.getOriginalUrl(),
            url.getCreatedAt(),
            url.getExpiresAt()
        );
    }

    public List<ShortenResponse> getMyUrls(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return urlRepository.findByUserId(user.getId())
            .stream()
            .map(u -> new ShortenResponse(
                u.getShortCode(),
                BASE_URL + u.getShortCode(),
                u.getOriginalUrl(),
                u.getCreatedAt(),
                u.getExpiresAt()
            ))
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUrl(String shortCode, String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        urlRepository.deleteByShortCodeAndUserId(shortCode, user.getId());
        redisService.delete(shortCode);
    }

    public String getOriginalUrl(String shortCode) {
        // 1. Check Redis first (fast path)
        String cached = redisService.get(shortCode);
        if (cached != null) {
            return cached;
        }

        // 2. Cache miss → check PostgreSQL
        Url url = urlRepository.findByShortCode(shortCode)
            .orElseThrow(() -> new RuntimeException("Short URL not found"));

        // 3. Re-cache for next time
        redisService.save(shortCode, url.getOriginalUrl(), 7);

        return url.getOriginalUrl();
    }

    private String generateUniqueCode() {
        SecureRandom random = new SecureRandom();
        String code;
        do {
            StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++) {
                sb.append(BASE62.charAt(random.nextInt(62)));
            }
            code = sb.toString();
        } while (urlRepository.existsByShortCode(code));
        return code;
    }
}