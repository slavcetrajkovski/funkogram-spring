package com.example.funkogram.app.category.service;

import com.example.funkogram.app.category.domain.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(String pattern, Long patternId);
    Category findById(Long id);

    List<String> getAllCategories();
}
