package com.example.Project_Online_market.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {
    
    @Value("${product.image.upload-dir}")
    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }
    
    public String uploadProductImage(MultipartFile file) throws IOException {
        System.out.println("=== Starting image upload process ===");
        System.out.println("Upload directory: " + uploadDir);
        
        // Create full path including the "products" subfolder
        Path uploadPath = Paths.get(uploadDir, "products");
        System.out.println("Absolute path: " + uploadPath.toAbsolutePath().toString());
        
        if (!Files.exists(uploadPath)) {
            System.out.println("Directory does not exist, attempting to create it...");
            try {
                Files.createDirectories(uploadPath);
                System.out.println("Directory created successfully");
            } catch (IOException e) {
                System.out.println("Failed to create directory: " + e.getMessage());
                throw new IOException("Failed to create directory for storing images: " + e.getMessage(), e);
            }
        } else {
            System.out.println("Directory already exists");
        }
        
        // Check if directory is writable
        if (!Files.isWritable(uploadPath)) {
            System.out.println("WARNING: Directory is not writable!");
            throw new IOException("Upload directory is not writable: " + uploadPath.toString());
        }
        
        // Get the original filename
        String originalFileName = file.getOriginalFilename();
        System.out.println("Original filename: " + originalFileName);
        
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            System.out.println("File extension: " + extension);
        } else {
            System.out.println("No file extension detected");
        }
        
        // Generate a unique filename to prevent overwriting
        String fileName = UUID.randomUUID().toString() + extension;
        System.out.println("Generated unique filename: " + fileName);
        
        // Define the path to save the file
        Path filePath = uploadPath.resolve(fileName);
        System.out.println("Full file path: " + filePath.toString());
        
        try {
            // Write the file to the server
            System.out.println("Transferring file...");
            file.transferTo(filePath.toFile());
            System.out.println("File transfer completed successfully");
            
            // Verify file was actually saved
            if (Files.exists(filePath)) {
                System.out.println("File verification: File exists at target location");
                System.out.println("File size: " + Files.size(filePath) + " bytes");
            } else {
                System.out.println("ERROR: File verification failed! File does not exist at target location");
                throw new IOException("File transfer appeared to succeed but file does not exist at target location");
            }
        } catch (IOException e) {
            System.out.println("ERROR during file transfer: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to save image file: " + e.getMessage(), e);
        }
        
        String storagePath = "/uploads/products/" + originalFileName;
        System.out.println("Path for database storage (using original filename): " + storagePath);
        System.out.println("=== Image upload process completed ===");
        
        return storagePath;
    }
}