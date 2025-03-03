package com.example.Project_Online_market.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Project_Online_market.Model.Reviews;

public interface ReviewRepository extends JpaRepository<Reviews, Integer> {
    
}
