// package com.example.Project_Online_market.Controller;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.example.Project_Online_market.Model.Tag;
// import com.example.Project_Online_market.Repository.TagsRespostory;

// import jakarta.validation.Valid;

// @RestController
// @RequestMapping("/api/tags")
// public class TagsController {

//     private final TagsRespostory tagsRepository;

//     public TagsController(TagsRespostory tagsRepository) {
//         this.tagsRepository = tagsRepository;
//     }

//     // GET all tags
//     @GetMapping
//     public ResponseEntity<List<Tag>> getAllTags() {
//         List<Tag> tagsList = tagsRepository.findAll();
//         return ResponseEntity.ok(tagsList);
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<Tag> getTagById(@PathVariable int id) {
//         Tag tag = tagsRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Tag not found"));
//         return ResponseEntity.ok(tag);
//     }

//     @PostMapping("/create")
//     public ResponseEntity<Tag> createTag(@Valid @RequestBody Tag tag) {
//         Tag savedTag = tagsRepository.save(tag);
//         return ResponseEntity.status(HttpStatus.CREATED).body(savedTag);
//     }

//     @PutMapping("/update/{id}")
//     public ResponseEntity<Tag> updateTag(@PathVariable int id, @Valid @RequestBody Tag tagDetails) {
//         Tag tag = tagsRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Tag not found"));
//         tag.setTagName(tagDetails.getTagName());
//         Tag updatedTag = tagsRepository.save(tag);
//         return ResponseEntity.ok(updatedTag);
//     }

//     // Delete a tag
//     @DeleteMapping("/delete/{id}")
//     public ResponseEntity<Map<String, String>> deleteTag(@PathVariable int id) {
//         Tag tag = tagsRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Tag not found"));
//         tagsRepository.delete(tag);
//         Map<String, String> response = new HashMap<>();
//         response.put("message", "Tag deleted successfully");
//         return ResponseEntity.ok(response);
//     }
// }


package com.example.Project_Online_market.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Online_market.Model.Tag;
import com.example.Project_Online_market.Repository.TagsRespostory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

/**
 * REST controller for managing Tag entities.
 * Provides CRUD operations for the Tag resource.
 */
@RestController
@RequestMapping("/api/tags")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags Management", description = "APIs for managing product tags")
public class TagsController {

    private final TagsRespostory tagsRepository;

    /**
     * Constructor for TagsController.
     * 
     * @param tagsRepository the repository to manage Tag entities
     */
    public TagsController(TagsRespostory tagsRepository) {
        this.tagsRepository = tagsRepository;
    }

    /**
     * Retrieves all tags from the database.
     * 
     * @return ResponseEntity containing a list of all tags
     */
    @Operation(summary = "Get all tags", description = "Retrieves a list of all available tags")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all tags",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tag.class)))
    })
    @GetMapping
    public ResponseEntity<List<Tag>> getAllTags() {
        List<Tag> tagsList = tagsRepository.findAll();
        return ResponseEntity.ok(tagsList);
    }

    /**
     * Retrieves a specific tag by its ID.
     * 
     * @param id the ID of the tag to retrieve
     * @return ResponseEntity containing the found tag
     * @throws RuntimeException if the tag is not found
     */
    @Operation(summary = "Get tag by ID", description = "Retrieves a specific tag by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the tag",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tag.class))),
        @ApiResponse(responseCode = "404", description = "Tag not found",
            content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTagById(
            @Parameter(description = "ID of the tag to retrieve", required = true) @PathVariable int id) {
        Tag tag = tagsRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tag not found"));
        return ResponseEntity.ok(tag);
    }

    /**
     * Creates a new tag.
     * 
     * @param tag the tag entity to create
     * @return ResponseEntity containing the created tag
     */
    @Operation(summary = "Create a new tag", description = "Creates a new tag in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tag successfully created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tag.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<Tag> createTag(
            @Parameter(description = "Tag object to be created", required = true, 
                schema = @Schema(implementation = Tag.class)) 
            @Valid @RequestBody Tag tag) {
        Tag savedTag = tagsRepository.save(tag);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTag);
    }

    /**
     * Updates an existing tag.
     * 
     * @param id the ID of the tag to update
     * @param tagDetails the updated tag details
     * @return ResponseEntity containing the updated tag
     * @throws RuntimeException if the tag is not found
     */
    @Operation(summary = "Update an existing tag", description = "Updates a tag based on the provided ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tag successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tag.class))),
        @ApiResponse(responseCode = "404", description = "Tag not found",
            content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content)
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<Tag> updateTag(
            @Parameter(description = "ID of the tag to update", required = true) @PathVariable int id, 
            @Parameter(description = "Updated tag details", required = true, 
                schema = @Schema(implementation = Tag.class)) 
            @Valid @RequestBody Tag tagDetails) {
        Tag tag = tagsRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tag not found"));
        tag.setTagName(tagDetails.getTagName());
        Tag updatedTag = tagsRepository.save(tag);
        return ResponseEntity.ok(updatedTag);
    }

    /**
     * Deletes a tag by its ID.
     * 
     * @param id the ID of the tag to delete
     * @return ResponseEntity with a success message
     * @throws RuntimeException if the tag is not found
     */
    @Operation(summary = "Delete a tag", description = "Deletes a tag based on the provided ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tag successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Tag not found",
            content = @Content)
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteTag(
            @Parameter(description = "ID of the tag to delete", required = true) @PathVariable int id) {
        Tag tag = tagsRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tag not found"));
        tagsRepository.delete(tag);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Tag deleted successfully");
        return ResponseEntity.ok(response);
    }
}