package com.example.funkogram.app.product.repository;

import com.example.funkogram.helpers.UrlPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlPatternRepository extends JpaRepository<UrlPattern, Long> {
}
