package com.example.Project_Online_market.Model;
import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Reviews")
public class Reviews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Review_ID;

    @NotNull(message = "Rating is Required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    @Column(name = "Rating", nullable = false)
    private int Rating;

    @NotBlank(message = "Comment is Required")
    @Size(min = 3, max = 200, message = "Comment must be between 3 and 200 characters")
    @Column(name = "Comment", nullable = false, length = 200)
    private String Comment;

    @NotNull(message = "Review Date is Required")
    @PastOrPresent(message = "Review Date cannot be in the future")
    @Column(name = "Review_Date", nullable = false)
    private Date Review_Date;

    // Many-to-one relationship with Users
    // @ManyToOne
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "User_ID", nullable = false)
    private Users user;

    // Many-to-one relationship with Users
    // @ManyToOne
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "Product_ID", nullable = false)
    private Products product;

    public int getReview_ID() {
        return Review_ID;
    }

    public void setReview_ID(int review_ID) {
        Review_ID = review_ID;
    }

    public int getRating() {
        return Rating;
    }

    public void setRating(int rating) {
        Rating = rating;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public Date getReview_Date() {
        return Review_Date;
    }

    public void setReview_Date(Date review_Date) {
        Review_Date = review_Date;
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



    
}
