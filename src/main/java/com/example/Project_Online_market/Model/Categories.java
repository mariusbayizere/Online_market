package com.example.Project_Online_market.Model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Categories")
public class Categories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Category_ID;

    @NotBlank(message = "Category Name is Required")
    @Size(min = 3, max = 50, message = "Category Name must be between 3 and 50 characters")
    @Column(name = "Category_Name", nullable = false, length = 50)
    private String category_Name;

    @NotBlank(message = "Category Description is Required")
    @Size(min = 3, max = 200, message = "Category Description must be between 3 and 200 characters")
    @Column(name = "Category_Description", nullable = false, length = 200)
    private String category_Description;

        // One-to-many relationship with Products
    // @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @OneToMany(mappedBy = "category", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.LAZY, orphanRemoval = false)
    @JsonIgnore
    private List<Products> products;

    public int getCategory_ID() {
        return Category_ID;
    }

    public void setCategory_ID(int category_ID) {
        Category_ID = category_ID;
    }

    public String getCategory_Name() {
        return category_Name;
    }

    public void setCategory_Name(String category_Name) {
        this.category_Name = category_Name;
    }

    public String getCategory_Description() {
        return category_Description;
    }

    public void setCategory_Description(String category_Description) {
        this.category_Description = category_Description;
    }

    public List<Products> getProducts() {
        return products;
    }

    public void setProducts(List<Products> products) {
        this.products = products;
    }

    

}


