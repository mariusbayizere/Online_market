package com.example.Project_Online_market.Model;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.Project_Online_market.Enum.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "Users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int User_ID;

    @NotBlank(message = "First Name is Required")
    @Size(min = 3, max = 50, message = "First Name must be between 3 and 50 characters")
    @Column(name = "First_Name", nullable = false, length = 50)
    private String First_Name;

    @NotBlank(message = "Last Name is Required")
    @Size(min = 3, max = 50, message = "Last Name must be between 3 and 50 characters")
    @Column(name = "Last_Name", nullable = false, length = 50)
    private String Last_Name;

    @NotBlank(message = "Email is Required")
    @Email(message = "Email is invalid")
    @Size(min = 5, max = 50, message = "Email must be between 5 and 50 characters")
    @Column(name = "Email", nullable = false, length = 50, unique = true)
    @Pattern(
        regexp = "^[a-z0-9+_.-]+@gmail\\.com$", 
        message = "Email must be a valid Gmail address in lowercase"
    )
    private String email;

    @NotBlank(message = "Password is Required")
    @Size(min = 8, max = 200, message = "Password must be between 8 and 200 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
        message = "Password must contain at least one uppercase letter, one lowercase letter, and one number"
    )
    @Column(name = "Password", nullable = false, length = 200)
    private String Password;


    @Transient
    private String confirmPassword;

    @Transient
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @AssertTrue(message = "Password and Confirm Password must match")
    public boolean isPasswordConfirmed() {
        if (confirmPassword == null || Password == null) {
            return false;
        }
        return passwordEncoder.matches(confirmPassword, Password);
    }

    
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole = UserRole.BUYER;

    // One-to-many relationship with Orders
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Orders> orders;

    // One-to-many relationship with Reviews
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Reviews> reviews;

    public int getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(int user_ID) {
        User_ID = user_ID;
    }

    public String getFirst_Name() {
        return First_Name;
    }

    public void setFirst_Name(String first_Name) {
        First_Name = first_Name;
    }

    public String getLast_Name() {
        return Last_Name;
    }

    public void setLast_Name(String last_Name) {
        Last_Name = last_Name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public UserRole getUserRole() {
        return userRole;
    }



    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public List<Orders> getOrders() {
        return orders;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }

    public List<Reviews> getReviews() {
        return reviews;
    }

    public void setReviews(List<Reviews> reviews) {
        this.reviews = reviews;
    }


    
}
