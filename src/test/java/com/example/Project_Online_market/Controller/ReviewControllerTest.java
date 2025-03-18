package com.example.Project_Online_market.Controller;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.Project_Online_market.Model.Products;
import com.example.Project_Online_market.Model.Reviews;
import com.example.Project_Online_market.Model.Users;
import com.example.Project_Online_market.Repository.ProductsRepository;
import com.example.Project_Online_market.Repository.ReviewRepository;
import com.example.Project_Online_market.Repository.UsersRopository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewRepository reviewRepository;

    @MockBean
    private UsersRopository userRepository;

    @MockBean
    private ProductsRepository productRepository;

    private Reviews review;
    private Users user;
    private Products product;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setUser_ID(1);
        user.setEmail("bayizeremarius@gmail.com");

        product = new Products();
        product.setProduct_ID("1");
        product.setProduct_Name("Test Product");

        review = new Reviews();
        review.setReview_ID(1);
        review.setRating(4);
        review.setComment("Excellent product!");
        review.setReview_Date(new java.util.Date());
        review.setUser(user);
        review.setProduct(product);
    }

    @Test
    void testGetAllReviews() throws Exception {
        when(reviewRepository.findAll()).thenReturn(Arrays.asList(review));

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].review_ID").value(1))
                .andExpect(jsonPath("$[0].rating").value(4))
                .andExpect(jsonPath("$[0].comment").value("Excellent product!"));
    }

    @Test
    void testGetAllReview() throws Exception {
        when(reviewRepository.findAll()).thenReturn(Arrays.asList(review));

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].review_ID").value(1))
                .andExpect(jsonPath("$[0].rating").value(4))
                .andExpect(jsonPath("$[0].comment").value("Excellent product!"));
    }

    @Test
    void testGetReviewById() throws Exception {
        when(reviewRepository.findById(1)).thenReturn(Optional.of(review));

        mockMvc.perform(get("/api/reviews/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.review_ID").value(1))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.comment").value("Excellent product!"));
    }

    @Test
    void testUpdateReview() throws Exception {
        when(reviewRepository.findById(1)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Reviews.class))).thenReturn(review);

        review.setComment("Updated comment");

        mockMvc.perform(put("/api/reviews/update/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.review_ID").value(1))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.comment").value("Updated comment"));
    }

    @Test
    void testGetReviewsById() throws Exception {
        when(reviewRepository.findById(1)).thenReturn(Optional.of(review));

        mockMvc.perform(get("/api/reviews/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.review_ID").value(1))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.comment").value("Excellent product!"));
    }

    @Test
    void testCreateReview() throws Exception {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(reviewRepository.save(any(Reviews.class))).thenReturn(review);

        mockMvc.perform(post("/api/reviews/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(review)))
                .andDo(print())  
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.review_ID").exists())  // Only checks if ID exists
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.comment").value("Excellent product!"));
    }


    @Test
    void testDeleteReview() throws Exception {
        when(reviewRepository.findById(1)).thenReturn(Optional.of(review));

        mockMvc.perform(delete("/api/reviews/delete/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("Review deleted successfully"));

        verify(reviewRepository, times(1)).delete(review);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
