package com.project.url_shortener.repository;

import com.project.url_shortener.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
    List<Url> findByUserId(Long userId);
    void deleteByShortCodeAndUserId(String shortCode, Long userId);
}