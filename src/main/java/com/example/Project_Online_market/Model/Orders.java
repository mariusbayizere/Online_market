package com.example.Project_Online_market.Model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Order_ID;

    @NotNull(message = "Order Date is Required")
    @FutureOrPresent(message = "Order Date cannot be in the past")
    @Column(name = "Order_Date", nullable = false)
    private Date Order_Date;

    @NotBlank(message = "Order Status is Required")
    @Size(min = 3, max = 50, message = "Order Status must be between 3 and 50 characters")
    @Pattern(regexp = "^(Pending|Shipped|Delivered|Cancelled)$", message = "Order Status must be one of: Pending, Shipped, Delivered, Cancelled")
    @Column(name = "Order_Status", nullable = false, length = 50)
    private String Order_Status;

    @NotNull(message = "Quantity is Required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 1000, message = "Quantity must not exceed 1000")
    @Column(name = "Quantity", nullable = false)
    private int Quantity;

        // Many-to-one relationship with Users
        // @ManyToOne
    // @ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "User_ID", nullable = false) 
    private Users user;

    // @ManyToOne
    // @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "Product_ID", nullable = false) // Foreign key column
    private Products product;

    // One-to-one relationship with Payment
    @OneToOne(mappedBy = "order", cascade=CascadeType.ALL)
    @JsonIgnore
    private Payment payment;

    public int getOrder_ID() {
        return Order_ID;
    }

    public void setOrder_ID(int order_ID) {
        Order_ID = order_ID;
    }

    public Date getOrder_Date() {
        return Order_Date;
    }

    public void setOrder_Date(Date order_Date) {
        Order_Date = order_Date;
    }

    public String getOrder_Status() {
        return Order_Status;
    }

    public void setOrder_Status(String order_Status) {
        Order_Status = order_Status;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

}
