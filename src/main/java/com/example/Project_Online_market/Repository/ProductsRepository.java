package com.example.Project_Online_market.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Project_Online_market.Enum.FeaturedStatus;
import com.example.Project_Online_market.Model.Products;

public interface ProductsRepository extends JpaRepository<Products, String> {
    List<Products> findByIsFeaturedTrue();
    List<Products> findByIsFeaturedFalse();
    List<Products> findByIsFeatured(FeaturedStatus isFeatured);
    
}
