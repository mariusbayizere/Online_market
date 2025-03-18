package com.example.Project_Online_market.Controller;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.Project_Online_market.Model.Categories;
import com.example.Project_Online_market.Repository.CategoryRepository;

class CategoriesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoriesController categoriesController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(categoriesController).build();
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategories() throws Exception {
        Categories category1 = new Categories();
        category1.setCategory_ID(1);
        category1.setCategory_Name("Electronics");
        category1.setCategory_Description("Electronic items");

        Categories category2 = new Categories();
        category2.setCategory_ID(2);
        category2.setCategory_Name("Clothing");
        category2.setCategory_Description("Clothing items");

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category_Name").value("Electronics"))
                .andExpect(jsonPath("$[1].category_Name").value("Clothing"));
    }

    @Test
    void getCategoryById_ShouldReturnCategory() throws Exception {
        Categories category = new Categories();
        category.setCategory_ID(1);
        category.setCategory_Name("Electronics");
        category.setCategory_Description("Electronic items");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category_Name").value("Electronics"));
    }

    @Test
    void addCategory_ShouldCreateCategory() throws Exception {
        Categories category = new Categories();
        category.setCategory_Name("Books");
        category.setCategory_Description("Books category");

        when(categoryRepository.save(any(Categories.class))).thenReturn(category);

        mockMvc.perform(post("/api/categories/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"category_Name\":\"Books\",\"category_Description\":\"Books category\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category_Name").value("Books"));
    }

    @Test
    void updateCategory_ShouldUpdateExistingCategory() throws Exception {
        Categories category = new Categories();
        category.setCategory_ID(1);
        category.setCategory_Name("Electronics");
        category.setCategory_Description("Electronic items");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Categories.class))).thenReturn(category);

        mockMvc.perform(put("/api/categories/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"category_Name\":\"Updated Electronics\",\"category_Description\":\"Updated description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category_Name").value("Updated Electronics"));
    }

    @Test
    void deleteCategory_ShouldRemoveCategory() throws Exception {
        Categories category = new Categories();
        category.setCategory_ID(1);
        category.setCategory_Name("Electronics");
        category.setCategory_Description("Electronic items");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(category);

        mockMvc.perform(delete("/api/categories/delete/1"))
                .andExpect(status().isNoContent());
    }
}
