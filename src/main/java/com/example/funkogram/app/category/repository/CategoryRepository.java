package com.example.funkogram.app.category.repository;

import com.example.funkogram.app.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
