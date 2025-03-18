package com.example.Project_Online_market.Controller;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.Project_Online_market.Enum.FeaturedStatus;
import com.example.Project_Online_market.Model.Categories;
import com.example.Project_Online_market.Model.Products;
import com.example.Project_Online_market.Model.Tag;
import com.example.Project_Online_market.Repository.CategoryRepository;
import com.example.Project_Online_market.Repository.ProductsRepository;
import com.example.Project_Online_market.Repository.TagsRespostory;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class ProductsControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ProductsRepository productsRepository;

    @Mock
    private CategoryRepository categoriesRepository;

    @Mock
    private TagsRespostory tagsRepository;

    @InjectMocks
    private ProductsController productsController;

    private Products simpleProduct;
    private Products simpleProducts;
    private Categories simpleCategory;
    private Tag simpleTag;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productsController).build();
        objectMapper = new ObjectMapper();

        simpleCategory = new Categories();
        simpleCategory.setCategory_ID(1);
        simpleCategory.setCategory_Name("Test Category");

        simpleTag = new Tag();
        simpleTag.setTagId(1);
        simpleTag.setTagName("Test Tag");

        simpleProduct = new Products();
        simpleProducts = new Products();
        simpleProduct.setProduct_ID("PROD001");
        simpleProducts.setProduct_ID("PROD002");

        simpleProduct.setProduct_Name("Simple Test Product");
        simpleProduct.setProduct_Description("A test product for unit testing");
        simpleProduct.setProduct_Price(10.0);
        simpleProduct.setProduct_Quantity(5);
        simpleProduct.setProduct_Image("https://example.com/image.jpg");
        simpleProduct.setCreate_date(new Date());
        simpleProduct.setExpiry_date(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        simpleProduct.setStatus("Active");
        simpleProduct.setIsFeatured(FeaturedStatus.NOT_FEATURED);
        simpleProduct.setCategory(simpleCategory);
        simpleProduct.setTags(Set.of(simpleTag));
    }

    @Test
    void testGetAllProducts() throws Exception {
        when(productsRepository.findAll()).thenReturn(Collections.singletonList(simpleProduct));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].product_ID").value("PROD001"));
    }
    

    @Test
    void testGetProductById() throws Exception {
        when(productsRepository.findById("PROD001")).thenReturn(Optional.of(simpleProduct));

        mockMvc.perform(get("/api/products/PROD001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.product_ID").value("PROD001"));
    }

    @Test
    void testGetProductsById() throws Exception {
        when(productsRepository.findById("PROD002")).thenReturn(Optional.of(simpleProducts));

        mockMvc.perform(get("/api/products/PROD002"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.product_ID").value("PROD002"));
    }
    @Test
    void testCreateProductWithMissingRequiredFields() throws Exception {
        Products invalidProduct = new Products();
        MockMultipartFile productPart = new MockMultipartFile(
                "product", 
                "", 
                "application/json", 
                objectMapper.writeValueAsString(invalidProduct).getBytes());
        
        mockMvc.perform(multipart("/api/products/add")
                .file(productPart))
                .andExpect(status().isBadRequest());
    }



    @Test
    void testDeleteProduct() throws Exception {
        when(productsRepository.findById("PROD001")).thenReturn(Optional.of(simpleProduct));
        doNothing().when(productsRepository).delete(simpleProduct);

        mockMvc.perform(delete("/api/products/delete/PROD001"))
                .andExpect(status().isOk());

        verify(productsRepository, times(1)).delete(simpleProduct);
    }

    @Test
    void testDeleteProducts() throws Exception {
        when(productsRepository.findById("PROD002")).thenReturn(Optional.of(simpleProducts));
        doNothing().when(productsRepository).delete(simpleProducts);

        mockMvc.perform(delete("/api/products/delete/PROD002"))
                .andExpect(status().isOk());

        verify(productsRepository, times(1)).delete(simpleProducts);
    }
}
