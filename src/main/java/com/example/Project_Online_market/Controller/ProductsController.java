package com.example.Project_Online_market.Controller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Project_Online_market.Enum.FeaturedStatus;
import com.example.Project_Online_market.Model.Categories;
import com.example.Project_Online_market.Model.Products;
import com.example.Project_Online_market.Model.Tag;
import com.example.Project_Online_market.Repository.CategoryRepository;
import com.example.Project_Online_market.Repository.ProductsRepository;
import com.example.Project_Online_market.Repository.TagsRespostory;
import com.example.Project_Online_market.exception.UserNotFoundException;
import com.example.Project_Online_market.service.ImageService;
// import com.fasterxml.jackson.core.JsonProcessingExcseption;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

    /**
     * Controller class responsible for handling product-related operations.
     * Provides endpoints for creating, updating, retrieving, and deleting products.
     */
    @RestController
    @RequestMapping("/api/products")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.tags.Tag(name = "Products", description = "Product management API")
    public class ProductsController {

        private final ProductsRepository productsRepository;
        private final ImageService imageService;


        /**
         * Constructor that initializes the ProductsController with the given ProductsRepository.
         *
         * @param productsRepository the repository to interact with the product data
         */
        public ProductsController(ProductsRepository productsRepository, ImageService imageService) {
            this.productsRepository = productsRepository;
            this.imageService = imageService;
        }

        @Autowired
        private CategoryRepository categoriesRepository;

        @Autowired
        private TagsRespostory tagsRepository;

        /**
         * Endpoint to retrieve all products.
         * 
         * @return ResponseEntity containing a list of all products
         */
        @Operation(summary = "Get all products", description = "Retrieves a list of all products")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all products", 
                        content = @Content(mediaType = "application/json", 
                        schema = @Schema(implementation = Products.class)))
        })
        @GetMapping
        public ResponseEntity<List<Products>> getAllProducts() {
            return ResponseEntity.ok(productsRepository.findAll());
        }

        /**
         * Endpoint to retrieve a product by its ID.
         * 
         * @param id the ID of the product to retrieve
         * @return ResponseEntity containing the product with the given ID
         * @throws UserNotFoundException if the product with the provided ID does not exist
         */
        @Operation(summary = "Get product by ID", description = "Retrieves a product by its ID")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the product", 
                        content = @Content(mediaType = "application/json", 
                        schema = @Schema(implementation = Products.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<Products> getProductById(
                @Parameter(description = "ID of the product to retrieve") @PathVariable String id) {
            Products product = productsRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("Product with id " + id + " not found"));
            return ResponseEntity.ok(product);
        }

        /**
         * Endpoint to delete a product by its ID.
         * 
         * @param id the ID of the product to delete
         * @return ResponseEntity containing a confirmation message
         * @throws UserNotFoundException if the product with the provided ID does not exist
         */
        @Operation(summary = "Delete a product", description = "Deletes a product by its ID")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the product"),
            @ApiResponse(responseCode = "404", description = "Product not found")
        })
        @DeleteMapping("/delete/{id}")
        public ResponseEntity<Map<String, String>> deleteProduct(
                @Parameter(description = "ID of the product to delete") @PathVariable String id) {
            Products product = productsRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("Product with id " + id + " not found"));
            productsRepository.delete(product);
    
    
            Map<String, String> response = new HashMap<>();
            response.put("message", "Product with id " + id + " has been successfully deleted");
            return ResponseEntity.ok(response);
        }
        
        /**
         * Endpoint to get all featured products.
         * 
         * @return ResponseEntity containing a list of all featured products
         */
        @Operation(summary = "Get featured products", description = "Retrieves a list of all featured products")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved featured products", 
                        content = @Content(mediaType = "application/json", 
                        schema = @Schema(implementation = Products.class)))
        })
        @GetMapping("/featured")
        public ResponseEntity<List<Products>> getFeaturedProducts() {
            List<Products> featuredProducts = productsRepository.findByIsFeatured(FeaturedStatus.FEATURED);
            return ResponseEntity.ok(featuredProducts);
        }

        /**
         * Endpoint to get all non-featured products (where featured is false).
         * 
         * @return ResponseEntity containing a list of all non-featured products
         */
        @Operation(summary = "Get non-featured products", description = "Retrieves a list of all non-featured products")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved non-featured products", 
                        content = @Content(mediaType = "application/json", 
                        schema = @Schema(implementation = Products.class)))
        })
        @GetMapping("/non-featured")
        public ResponseEntity<List<Products>> getNonFeaturedProducts() {
            List<Products> nonFeaturedProducts = productsRepository.findByIsFeatured(FeaturedStatus.NOT_FEATURED);
            return ResponseEntity.ok(nonFeaturedProducts);
        }
        
        /**
         * Endpoint to add a new product.
         * 
         * @param product the product object to add
         * @return ResponseEntity containing the newly created product
         */
        @Operation(summary = "Add a new product", description = "Creates a new product")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the product", 
                        content = @Content(mediaType = "application/json", 
                        schema = @Schema(implementation = Products.class))),
            @ApiResponse(responseCode = "400", description = "Invalid product data provided"),
            @ApiResponse(responseCode = "409", description = "Product ID already exists")
        })
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(
            @RequestPart("product") String productJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
    
        System.out.println("=== Starting product addition process ===");
        System.out.println("Received product JSON: " + productJson);
        
        if (imageFile != null) {
            System.out.println("Received image file: " + imageFile.getOriginalFilename() 
                    + ", size: " + imageFile.getSize() 
                    + ", content type: " + imageFile.getContentType());
        } else {
            System.out.println("WARNING: No image file received in the request");
        }
    
        try {
            // Convert the JSON string to Products object
            System.out.println("Attempting to parse product JSON...");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // For handling date serialization
            Products product = objectMapper.readValue(productJson, Products.class);
            System.out.println("Successfully parsed product: " + product.getProduct_ID());
            
            // Perform all other validations
            if (product.getProduct_ID() == null || product.getProduct_ID().isEmpty()) {
                return ResponseEntity.badRequest().body("Product ID is required.");
            }
    
            if (productsRepository.existsById(product.getProduct_ID())) {
                return ResponseEntity.status(409).body("Product ID already exists.");
            }
    
            if (product.getCategory() == null || product.getCategory().getCategory_ID() <= 0) {
                return ResponseEntity.badRequest().body("Valid Category ID is required.");
            }
    
            Optional<Categories> categoryOpt = categoriesRepository.findById(product.getCategory().getCategory_ID());
            if (categoryOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Category with ID " + product.getCategory().getCategory_ID() + " not found.");
            }
            product.setCategory(categoryOpt.get());
    
            if (product.getTags() != null && !product.getTags().isEmpty()) {
                Set<Tag> resolvedTags = new HashSet<>();
                for (Tag tag : product.getTags()) {
                    if (tag.getTagId() == null || tag.getTagId() <= 0) {
                        return ResponseEntity.badRequest().body("Invalid tag ID: " + tag.getTagId());
                    }
                    Optional<Tag> existingTag = tagsRepository.findById(tag.getTagId());
                    if (existingTag.isEmpty()) {
                        return ResponseEntity.badRequest().body("Tag with ID " + tag.getTagId() + " not found.");
                    }
                    resolvedTags.add(existingTag.get());
                }
                product.setTags(resolvedTags);
            }
    
            if (product.getProduct_Price() == null || product.getProduct_Price() < 0) {
                return ResponseEntity.badRequest().body("Product price must be a positive value.");
            }
            if (product.getProduct_Quantity() == null || product.getProduct_Quantity() < 0) {
                return ResponseEntity.badRequest().body("Product quantity must be a positive value.");
            }
    
            if (product.getIsFeatured() == FeaturedStatus.FEATURED) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                    product.setIsFeatured(FeaturedStatus.NOT_FEATURED);
                }
            }
            
            // Modified image handling logic
            System.out.println("Processing image...");
            // Check if an image was provided in the request
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    System.out.println("Image file found in request, uploading...");
                    // Upload the image
                    String imagePath = imageService.uploadProductImage(imageFile);
                    System.out.println("Image uploaded successfully to: " + imagePath);
                    // Set the image path on the product
                    product.setProduct_Image(imagePath);
                } catch (IOException e) {
                    System.out.println("ERROR during image upload: " + e.getMessage());
                    e.printStackTrace();
                    return ResponseEntity.status(500).body("Error uploading image: " + e.getMessage());
                }
            } 
            // Check if an image path was already set in the JSON
            else if (product.getProduct_Image() != null && !product.getProduct_Image().isEmpty()) {
                System.out.println("Using image path from JSON: " + product.getProduct_Image());
                // The product already has an image path set, so we'll use that
            } 
            // No image provided at all
            else {
                System.out.println("No image provided in request or JSON");
                // OPTION 1: Return an error (uncomment if you want this behavior)
                // return ResponseEntity.badRequest().body("Product image is required.");
                
                // OPTION 2: Set a default image path (more user-friendly)
                String defaultImagePath = "/uploads/products/default-product.jpg";
                System.out.println("Setting default image path: " + defaultImagePath);
                product.setProduct_Image(defaultImagePath);
            }
            
            // Save the product
            System.out.println("Saving product to database...");
            Products savedProduct = productsRepository.save(product);
            System.out.println("Product saved successfully with ID: " + savedProduct.getProduct_ID());
            return ResponseEntity.status(201).body(savedProduct);
    
        } catch (Exception e) {
            System.out.println("UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error saving product: " + e.getMessage());
        } finally {
            System.out.println("=== Product addition process completed ===");
        }
    }



        /**
         * Endpoint to update a product by its ID.
         * 
         * @param id the ID of the product to update
         * @param productDetails the product object containing updated data
         * @return ResponseEntity containing the updated product
         */
        @Operation(summary = "Update a product", description = "Updates an existing product by its ID")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the product", 
                        content = @Content(mediaType = "application/json", 
                        schema = @Schema(implementation = Products.class))),
            @ApiResponse(responseCode = "404", description = "Product or category not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only admins can update featured status")
        })
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(
            @Parameter(description = "ID of the product to update") @PathVariable String id, 
            @Parameter(description = "Updated product details", required = true)
            @Valid @RequestBody Products productDetails) {
        Optional<Products> existingProductOpt = productsRepository.findById(id);
        if (existingProductOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Product not found");
        }
    
        Products existingProduct = existingProductOpt.get();
        boolean changingFeaturedStatus = productDetails.getIsFeatured() != null
                                            && !productDetails.getIsFeatured().equals(existingProduct.getIsFeatured());
    
        if (changingFeaturedStatus) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(403).body("Only admins can update the featured status.");
            }
            existingProduct.setIsFeatured(productDetails.getIsFeatured());
        }

        if (productDetails.getCategory() != null && productDetails.getCategory().getCategory_ID() > 0) {
            Optional<Categories> categoryOpt = categoriesRepository.findById(productDetails.getCategory().getCategory_ID());
            if (categoryOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Category not found");
            }
            existingProduct.setCategory(categoryOpt.get());
        }
        existingProduct.setProduct_Name(productDetails.getProduct_Name());
        existingProduct.setProduct_Description(productDetails.getProduct_Description());
        existingProduct.setProduct_Price(productDetails.getProduct_Price());
        existingProduct.setProduct_Quantity(productDetails.getProduct_Quantity());
        existingProduct.setProduct_Image(productDetails.getProduct_Image());
        existingProduct.setCreate_date(productDetails.getCreate_date());
        existingProduct.setExpiry_date(productDetails.getExpiry_date());
        existingProduct.setStatus(productDetails.getStatus());

        Products updatedProduct = productsRepository.save(existingProduct);
        return ResponseEntity.ok(updatedProduct);
    }
        

        /**
         * Endpoint to browse products with filtering options.
         * 
         * @param categoryId optional parameter to filter products by category ID
         * @param tagId optional parameter to filter products by tag ID
         * @param keyword optional parameter to search products by keyword
         * @return ResponseEntity containing a list of filtered products
         */
        @Operation(summary = "Browse products", description = "Browse products with optional filtering by category, tag, or keyword search")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered products", 
                        content = @Content(mediaType = "application/json", 
                        schema = @Schema(implementation = Products.class)))
        })
        @GetMapping("/browse")
        public ResponseEntity<List<Products>> browseProducts(
            @Parameter(description = "Filter by category ID") @RequestParam(required = false) Integer categoryId,
            @Parameter(description = "Filter by tag ID") @RequestParam(required = false) Integer tagId,
            @Parameter(description = "Search by keyword") @RequestParam(required = false) String keyword) {
        
        List<Products> products = productsRepository.findAll();
        if (categoryId != null) {
            products = products.stream()
                .filter(p -> p.getCategory() != null && p.getCategory().getCategory_ID() == categoryId)
                .collect(Collectors.toList());
        }
        if (tagId != null) {
            products = products.stream()
                .filter(p -> p.getTags() != null &&
                        p.getTags().stream().anyMatch(tag -> tag.getTagId() == tagId))
                .collect(Collectors.toList());
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            products = products.stream()
                .filter(p -> p.getProduct_Name().toLowerCase().contains(lowerKeyword)
                        || p.getProduct_Description().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
        }
        return ResponseEntity.ok(products);
        }    
    }