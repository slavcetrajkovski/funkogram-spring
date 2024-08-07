package com.example.funkogram.app.category.service.impl;

import com.example.funkogram.app.category.domain.Category;
import com.example.funkogram.app.category.repository.CategoryRepository;
import com.example.funkogram.app.category.service.CategoryService;
import com.example.funkogram.app.exceptions.NotFoundEntityException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @Override
    public Category createCategory(String pattern, Long patternId) {
        Category category = this.findById(patternId);
        if(category != null) {
            return category;
        }
        if(pattern.contains("television")){
            category = new Category("Television");
        } else if(pattern.contains("limited")) {
            category = new Category("Exclusives");
        } else if(pattern.contains("marvel")) {
            category = new Category("Marvel");
        } else if(pattern.contains("animation")) {
            category = new Category("Animation");
        } else if (pattern.contains("star-wars")) {
            category = new Category("Star Wars");
        } else if(pattern.contains("movies")) {
            category = new Category("Movies");
        } else if(pattern.contains("disney")) {
            category = new Category("Disney");
        } else if(pattern.contains("Bitty")) {
            category = new Category("Bitty Pop");
        }
        else {
            category = new Category("Other");
        }

        return this.categoryRepository.save(category);
    }

    @Override
    public Category findById(Long id) {
        return this.categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException(Category.class, id));
    }

    @Override
    public List<String> getAllCategories() {
        return categoryRepository.findAll().stream().map(Category::getName).collect(Collectors.toList());
    }
}
