package com.example.funkogram.app.category.repository;

import com.example.funkogram.app.category.domain.Category;
import com.example.funkogram.app.category.domain.CategoryProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryProductRepository extends JpaRepository<CategoryProduct, Long> {

    @Query("SELECT c FROM Category c " +
            "JOIN CategoryProduct cp ON c.id = cp.id.categoryId " +
            "WHERE cp.id.productId = :productId")
    List<Category> findCategoriesByProductId(@Param("productId") Long productId);
}
