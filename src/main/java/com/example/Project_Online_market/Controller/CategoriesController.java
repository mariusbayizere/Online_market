package com.example.Project_Online_market.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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

import com.example.Project_Online_market.Model.Categories;
import com.example.Project_Online_market.Repository.CategoryRepository;
import com.example.Project_Online_market.exception.UserNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/categories")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Category Management", description = "Endpoints for managing categories") // Swagger tag
public class CategoriesController {

    private final CategoryRepository categoriesRepository;

    public CategoriesController(CategoryRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    @Operation(summary = "Retrieve all categories", description = "Returns a list of all available categories.")
    @GetMapping
    public ResponseEntity<List<Categories>> getAllCategories() {
        return ResponseEntity.ok(categoriesRepository.findAll());
    }

    @Operation(summary = "Get category by ID", description = "Fetch a specific category by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Categories> getCategoryById(@PathVariable int id) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Category with ID " + id + " not found"));
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Add a new category", description = "Create and store a new category.")
    @PostMapping("/add")
    public ResponseEntity<Categories> addCategory(@RequestBody Categories category) {
        Categories newCategory = categoriesRepository.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCategory);
    }

    @Operation(summary = "Update category by ID", description = "Update an existing category using its ID.")
    @PutMapping("/update/{id}")
    public ResponseEntity<Categories> updateCategory(@PathVariable int id, @RequestBody Categories category) {
        Categories existingCategory = categoriesRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Category with ID " + id + " not found"));

        existingCategory.setCategory_Name(category.getCategory_Name());
        existingCategory.setCategory_Description(category.getCategory_Description());

        Categories updatedCategory = categoriesRepository.save(existingCategory);
        return ResponseEntity.ok(updatedCategory);
    }

    @Operation(summary = "Delete category by ID", description = "Remove a category permanently from the database.")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        Categories existingCategory = categoriesRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Category with ID " + id + " not found"));

        categoriesRepository.delete(existingCategory);
        return ResponseEntity.noContent().build();
    }
}
