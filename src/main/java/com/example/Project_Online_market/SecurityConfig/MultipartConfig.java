package com.example.Project_Online_market.SecurityConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MultipartConfig implements WebMvcConfigurer {

    @Value("${product.image.upload-dir}")
    private String uploadDir;

    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // This maps the URL path to the physical location where images are stored
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
        
        System.out.println("Resource handler mapped /uploads/** to file:" + uploadDir + "/");
    }
}