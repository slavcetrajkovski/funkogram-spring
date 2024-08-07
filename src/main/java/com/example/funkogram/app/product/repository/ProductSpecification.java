package com.example.funkogram.app.product.repository;

import com.example.funkogram.app.category.domain.Category;
import com.example.funkogram.app.product.domain.Product;
import com.example.funkogram.app.category.domain.CategoryProduct;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

@Slf4j
public class ProductSpecification {

    public static Specification<Product> hasCategory(String categoryName) {
        return (root, query, criteriaBuilder) -> {
            if (categoryName == null || categoryName.isEmpty() || categoryName.equals("Сите")) {
                return criteriaBuilder.conjunction();
            }
            Join<Product, CategoryProduct> categoryProductJoin = root.join("categoryProducts", JoinType.INNER);
            Join<CategoryProduct, Category> categoryJoin = categoryProductJoin.join("category", JoinType.INNER);
            return criteriaBuilder.equal(categoryJoin.get("name"), categoryName);
        };
    }

    public static Specification<Product> hasNameContaining(String searchFilter) {
        return (root, query, criteriaBuilder) -> {
            if (searchFilter == null || searchFilter.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + searchFilter.toLowerCase() + "%");
        };
    }

    public static Specification<Product> hasSort(String sortFilter) {
        return (root, query, criteriaBuilder) -> {
            if (sortFilter == null || sortFilter.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            if (sortFilter.equals("Најниска цена")) {
                query.orderBy(criteriaBuilder.asc(root.get("price")));
            } else if (sortFilter.equals("Највисока цена")) {
                query.orderBy(criteriaBuilder.desc(root.get("price")));
            }

            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Product> hasProductStatus(String productStatus) {
        return (root, query, criteriaBuilder) -> {
          if(productStatus == null || productStatus.isEmpty()) {
              return criteriaBuilder.notEqual(root.get("productStatus"), "AVAILABLE_RIGHT_AWAY");
          }

          return criteriaBuilder.equal(root.get("productStatus"), productStatus);
        };
    }

    public static Specification<Product> hasProductType(String productType) {
        return (root, query, criteriaBuilder) -> {
            if(productType == null || productType.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("productType"), productType);
        };
    }
}
