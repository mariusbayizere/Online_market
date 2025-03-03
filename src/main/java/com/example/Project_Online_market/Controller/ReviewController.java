// package com.example.Project_Online_market.Controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.example.Project_Online_market.Model.Products;
// import com.example.Project_Online_market.Model.Reviews;
// import com.example.Project_Online_market.Model.Users;
// import com.example.Project_Online_market.Repository.ProductsRepository;
// import com.example.Project_Online_market.Repository.ReviewRepository;
// import com.example.Project_Online_market.Repository.UsersRopository;

// import jakarta.validation.Valid;

// @RestController
// @RequestMapping("/api/reviews")
// @PreAuthorize("hasRole('ADMIN')")
// public class ReviewController {
    
//     @Autowired
//     private ReviewRepository reviewRepository;

//     @Autowired
//     private UsersRopository userRepository;

//     @Autowired
//     private ProductsRepository productRepository;

//     // ✅ 1. Get all reviews (Accessible to everyone)
//     @GetMapping
//     public List<Reviews> getAllReviews() {
//         return reviewRepository.findAll();
//     }

//     // ✅ 2. Get review by ID (Accessible to everyone)
//     @GetMapping("/{id}")
//     public ResponseEntity<Reviews> getReviewById(@PathVariable int id) {
//         Reviews review = reviewRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Review not found"));
//         return ResponseEntity.ok(review);
//     }

//     // ✅ 3. Create a new review (Accessible to authenticated users)
//     @PostMapping("/add")
//     // @PreAuthorize("isAuthenticated()")
//     public ResponseEntity<?> createReview(@Valid @RequestBody Reviews review) {
//         // Ensure user exists
//         Users user = userRepository.findById(review.getUser().getUser_ID())
//                 .orElseThrow(() -> new RuntimeException("User not found"));

//         // Ensure product exists
//         Products product = productRepository.findById(review.getProduct().getProduct_ID())
//                 .orElseThrow(() -> new RuntimeException("Product not found"));

//         review.setUser(user);
//         review.setProduct(product);

//         Reviews savedReview = reviewRepository.save(review);
//         return ResponseEntity.ok(savedReview);
//     }
//     @PutMapping("/update/{id}")
//     // @PreAuthorize("isAuthenticated()") 
//     public ResponseEntity<?> updateReview(@PathVariable int id, @Valid @RequestBody Reviews updatedReview) {
//         Reviews existingReview = reviewRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Review not found"));

//         existingReview.setRating(updatedReview.getRating());
//         existingReview.setComment(updatedReview.getComment());
//         existingReview.setReview_Date(updatedReview.getReview_Date());

//         Reviews savedReview = reviewRepository.save(existingReview);
//         return ResponseEntity.ok(savedReview);
//     }

//     // ✅ 5. Delete a review (Accessible only to ADMINs)
//     @DeleteMapping("/delete/{id}")
//     // @PreAuthorize("hasRole('ADMIN')") 
//     public ResponseEntity<?> deleteReview(@PathVariable int id) {
//         Reviews existingReview = reviewRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Review not found"));

//         reviewRepository.delete(existingReview);
//         return ResponseEntity.ok("Review deleted successfully");
//     }
// }


// ================

package com.example.Project_Online_market.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Online_market.Model.Products;
import com.example.Project_Online_market.Model.Reviews;
import com.example.Project_Online_market.Model.Users;
import com.example.Project_Online_market.Repository.ProductsRepository;
import com.example.Project_Online_market.Repository.ReviewRepository;
import com.example.Project_Online_market.Repository.UsersRopository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review Management", description = "Operations for managing product reviews")
public class ReviewController {
    
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UsersRopository userRepository;

    @Autowired
    private ProductsRepository productRepository;

    @Operation(
        summary = "Get all reviews",
        description = "Retrieves a list of all reviews in the system",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Successfully retrieved all reviews",
                content = @Content(schema = @Schema(implementation = Reviews.class))
            )
        }
    )
    @GetMapping
    public List<Reviews> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Operation(
        summary = "Get review by ID",
        description = "Retrieves a specific review by its ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the review"),
            @ApiResponse(responseCode = "404", description = "Review not found")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Reviews> getReviewById(@PathVariable int id) {
        Reviews review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        return ResponseEntity.ok(review);
    }

    @Operation(
        summary = "Create a new review",
        description = "Creates a new product review",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Review created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User or product not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
        }
    )
    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createReview(@Valid @RequestBody Reviews review) {
        Users user = userRepository.findById(review.getUser().getUser_ID())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Products product = productRepository.findById(review.getProduct().getProduct_ID())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        review.setUser(user);
        review.setProduct(product);

        Reviews savedReview = reviewRepository.save(review);
        return ResponseEntity.ok(savedReview);
    }

    @Operation(
        summary = "Update a review",
        description = "Updates an existing review",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
        }
    )
    @PutMapping("/update/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateReview(@PathVariable int id, @Valid @RequestBody Reviews updatedReview) {
        Reviews existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        existingReview.setRating(updatedReview.getRating());
        existingReview.setComment(updatedReview.getComment());
        existingReview.setReview_Date(updatedReview.getReview_Date());

        Reviews savedReview = reviewRepository.save(existingReview);
        return ResponseEntity.ok(savedReview);
    }

    @Operation(
        summary = "Delete a review",
        description = "Deletes an existing review by ID",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
        }
    )
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteReview(@PathVariable int id) {
        Reviews existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        reviewRepository.delete(existingReview);
        return ResponseEntity.ok("Review deleted successfully");
    }
}