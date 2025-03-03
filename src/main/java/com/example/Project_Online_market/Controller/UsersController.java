package com.example.Project_Online_market.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project_Online_market.Model.Users;
import com.example.Project_Online_market.Repository.UsersRopository;
import com.example.Project_Online_market.exception.UserNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;

/**
 * Controller class responsible for handling user-related operations.
 * Provides endpoints for creating, updating, retrieving, and deleting users.
 */
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Users Management", description = "APIs for managing users")
public class UsersController {

    private final UsersRopository usersRepository;

    /**
     * Constructor that initializes the UsersController with the given UsersRepository.
     *
     * @param usersRepository the repository to interact with the user data
     */
    public UsersController(UsersRopository usersRepository) {
        this.usersRepository = usersRepository;
    }

    /**
     * Endpoint to retrieve all users.
     * 
     * @return ResponseEntity containing a list of all users
     */
    @Operation(summary = "Get all users", description = "Retrieves a list of all registered users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all users",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Users.class)))
    })
    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(usersRepository.findAll());
    }

    /**
     * Endpoint to retrieve a user by their ID.
     * 
     * @param id the ID of the user to retrieve
     * @return ResponseEntity containing the user with the given ID
     * @throws UserNotFoundException if the user with the provided ID does not exist
     */
    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the user",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Users.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(
            @Parameter(description = "ID of the user to retrieve", required = true) @PathVariable int id) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        return ResponseEntity.ok(user);
    }
    
    /**
     * Endpoint to update a user by their ID.
     * 
     * @param id the ID of the user to update
     * @param users the user object containing updated data
     * @return ResponseEntity containing the updated user
     * @throws UserNotFoundException if the user with the provided ID does not exist
     */
    @Operation(summary = "Update an existing user", description = "Updates a user based on the provided ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Users.class))),
        @ApiResponse(responseCode = "400", description = "Email already taken or invalid input",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content)
    })
    @Transactional
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "ID of the user to update", required = true) @PathVariable int id, 
            @RequestBody Map<String, String> updates) {
        
        try {
            Users existingUser = usersRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
            
            System.out.println("Existing User Before Update: " + existingUser);
            System.out.println("Incoming Updates: " + updates);
            
            String newEmail = updates.get("email");
            if (newEmail != null && !newEmail.isEmpty()) {
                // Check if the new email is different from current and already exists
                if (!newEmail.equals(existingUser.getEmail()) && usersRepository.existsByEmail(newEmail)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is already taken.");
                }
                existingUser.setEmail(newEmail);
            }
            
            // Update first name if provided
            String firstName = updates.get("firstName");
            if (firstName != null && !firstName.isEmpty()) {
                existingUser.setFirst_Name(firstName);
            }
            
            String lastName = updates.get("lastName");
            if (lastName != null && !lastName.isEmpty()) {
                existingUser.setLast_Name(lastName);
            }

            Users savedUser = usersRepository.saveAndFlush(existingUser);
            
            System.out.println("User after update: " + savedUser);
            
            return ResponseEntity.ok(savedUser);
            
        } catch (DataIntegrityViolationException e) {
            System.err.println("Data integrity violation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data integrity violation: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Endpoint to delete a user by their ID.
     * 
     * @param id the ID of the user to delete
     * @return ResponseEntity containing a confirmation message
     * @throws UserNotFoundException if the user with the provided ID does not exist
     */
    @Operation(summary = "Delete a user", description = "Deletes a user based on the provided ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully deleted"),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content)
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @Parameter(description = "ID of the user to delete", required = true) @PathVariable int id) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        usersRepository.delete(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User with id " + id + " has been successfully deleted");
        return ResponseEntity.ok(response);
    }
}