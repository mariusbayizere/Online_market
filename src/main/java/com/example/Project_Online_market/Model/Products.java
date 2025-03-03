package com.example.Project_Online_market.Model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.Project_Online_market.Enum.FeaturedStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "Products")
public class Products {

    @Id
    @NotBlank(message = "Product ID is Required")
    @Size(min = 3, max = 10, message = "Product ID must be between 3 and 10 characters")
    @Column(name = "Product_ID", nullable = false, unique=true ,length = 10)
    private String Product_ID;

    @NotBlank(message = "Product Name is Required")
    @Size(min = 3, max = 50, message = "Product Name must be between 3 and 50 characters")
    @Column(name = "Product_Name", nullable = false, length = 50)
    private String Product_Name;

    @NotBlank(message = "Product Description is Required")
    @Size(min = 3, max = 200, message = "Product Description must be between 3 and 200 characters")
    @Column(name = "Product_Description", nullable = false, length = 200)
    private String Product_Description;

    @NotNull(message = "Product Price is Required")
    @Min(value = 0, message = "Product Price must be positive")
    @Column(name = "Product_Price", nullable = false)
    private double Product_Price;

    @Min(value = 1, message = "Product Quantity must be at least 1")
    @Max(value = 100, message = "Product Quantity must not exceed 100")
    @Column(name = "Product_Quantity", nullable = false)
    private int Product_Quantity;

    @NotBlank(message = "Product Image is required")
    @Pattern(regexp = "^https?://.*", message = "Product Image URL must be valid (http/https)")
    @Column(name = "Product_Image", nullable = false)
    private String Product_Image;

    @NotNull(message = "Create Date is required")
    @PastOrPresent(message = "Create Date cannot be in the future")
    @Column(name = "Create_date", nullable = false)
    private Date Create_date;

    @NotNull(message = "Expiry Date is required")
    @FutureOrPresent(message = "Expiry Date cannot be in the past")
    @Column(name = "Expiry_date", nullable = false)
    private Date expiry_date;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(Active|Inactive|Pending)$", message = "Status must be either Active, Inactive, or Pending")
    @Column(name = "Status", nullable = false)
    private String Status;

    @Enumerated(EnumType.STRING)
    @Column(name = "Is_Featured", nullable = false)
    private FeaturedStatus isFeatured;



    // @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY ,cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JsonIgnore
    private List<Orders> orders;

    // @ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "Category_ID", nullable = false)  // Foreign key column in the Products table
    private Categories category;

    // @OneToMany(mappedBy = "product", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, orphanRemoval = false)
    @JsonIgnore
    private List<Reviews> reviews;


    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "product_tags",
        joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "Product_ID"),
        inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();



    public String getProduct_ID() {
        return Product_ID;
    }

    public void setProduct_ID(String product_ID) {
        Product_ID = product_ID;
    }

    public String getProduct_Name() {
        return Product_Name;
    }

    public void setProduct_Name(String product_Name) {
        Product_Name = product_Name;
    }

    public String getProduct_Description() {
        return Product_Description;
    }

    public void setProduct_Description(String product_Description) {
        Product_Description = product_Description;
    }

    public double getProduct_Price() {
        return Product_Price;
    }

    public void setProduct_Price(double product_Price) {
        Product_Price = product_Price;
    }

    public int getProduct_Quantity() {
        return Product_Quantity;
    }

    public void setProduct_Quantity(int product_Quantity) {
        Product_Quantity = product_Quantity;
    }

    public String getProduct_Image() {
        return Product_Image;
    }

    public void setProduct_Image(String product_Image) {
        Product_Image = product_Image;
    }

    public Date getCreate_date() {
        return Create_date;
    }

    public void setCreate_date(Date create_date) {
        Create_date = create_date;
    }

    public Date getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(Date expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public List<Orders> getOrders() {
        return orders;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }

    public Categories getCategory() {
        return category;
    }

    public void setCategory(Categories category) {
        this.category = category;
    }

    public List<Reviews> getReviews() {
        return reviews;
    }

    public void setReviews(List<Reviews> reviews) {
        this.reviews = reviews;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public FeaturedStatus getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(FeaturedStatus isFeatured) {
        this.isFeatured = isFeatured;
    }
}
