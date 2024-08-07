package com.example.funkogram.app.category.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "id.categoryId")
    @JsonIgnore
    private List<CategoryProduct> categoryProducts;

    public Category(String name) {
        this.name = name;
        this.categoryProducts = new ArrayList<>();
    }
}
