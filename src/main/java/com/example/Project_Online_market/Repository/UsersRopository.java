package com.example.Project_Online_market.Repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Project_Online_market.Model.Users;


public interface UsersRopository  extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email); 
}
