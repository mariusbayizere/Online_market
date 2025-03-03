package com.example.Project_Online_market.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Project_Online_market.Model.Orders;
import com.example.Project_Online_market.Model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByOrder(Orders order);
    
}
