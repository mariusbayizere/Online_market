package com.example.Project_Online_market.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Project_Online_market.Model.Orders;
import com.example.Project_Online_market.Model.Products;
import com.example.Project_Online_market.Model.Users;

public interface OrdersRepository  extends JpaRepository<Orders, Integer> {
    Optional<Orders> findByUserAndProduct(Users user, Products product);
    List<Orders> findByUser(Users user);

    
}
