package com.example.Project_Online_market.Repository;

// import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Project_Online_market.Model.Categories;

public interface CategoryRepository extends JpaRepository<Categories, Integer> {
    
    // Optional<Categories> findByCategory_Name(String category_Name);
}