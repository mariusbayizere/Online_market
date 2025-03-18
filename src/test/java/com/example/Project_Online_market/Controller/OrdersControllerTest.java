package com.example.Project_Online_market.Controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.springframework.http.ResponseEntity;

import com.example.Project_Online_market.Model.Orders;
import com.example.Project_Online_market.Model.Products;
import com.example.Project_Online_market.Model.Users;
import com.example.Project_Online_market.Repository.OrdersRepository;
import com.example.Project_Online_market.Repository.ProductsRepository;
import com.example.Project_Online_market.Repository.UsersRopository;
import com.example.Project_Online_market.response.ApiResponses;

@ExtendWith(MockitoExtension.class)
public class OrdersControllerTest {
    
    @Mock
    private OrdersRepository ordersRepository;
    
    @Mock
    private UsersRopository usersRepository;
    
    @Mock
    private ProductsRepository productsRepository;
    
    @InjectMocks
    private OrdersController ordersController;
    
    private Orders order;
    private Users user;
    private Products product;
    
    @BeforeEach
    void setUp() {
        user = new Users();
        product = new Products();
        order = new Orders();
        order.setOrder_ID(1);
        order.setOrder_Status("Pending");
        order.setQuantity(2);
        order.setUser(user);
        order.setProduct(product);
    }
    
    @Test
    void testGetAllOrders() {
        List<Orders> ordersList = Arrays.asList(order);
        when(ordersRepository.findAll()).thenReturn(ordersList);
        ResponseEntity<List<Orders>> response = ordersController.getAllOrders();
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }
    
    @Test
    void testGetOrderById_Found() {
        when(ordersRepository.findById(1)).thenReturn(Optional.of(order));
        ResponseEntity<?> response = ordersController.getOrderById(1);
        
        assertEquals(200, response.getStatusCodeValue());
    }
    
    @Test
    void testGetOrderById_NotFound() {
        when(ordersRepository.findById(2)).thenReturn(Optional.empty());
        ResponseEntity<?> response = ordersController.getOrderById(2);
        
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testDeleteOrder() {
        Orders order = new Orders();
        order.setOrder_ID(1); 

        when(ordersRepository.existsById(1)).thenReturn(true); 
        doNothing().when(ordersRepository).deleteById(1);

        ResponseEntity<?> response = ordersController.deleteOrder(1);

        assertEquals(200, response.getStatusCodeValue());

        String responseMessage = ((com.example.Project_Online_market.response.ApiResponses) response.getBody()).getMessage();
        assertEquals("Order deleted successfully", responseMessage); 

        verify(ordersRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteOrder_NotFound() {
        when(ordersRepository.existsById(1)).thenReturn(false);

        ResponseEntity<?> response = ordersController.deleteOrder(1);

        assertEquals(404, response.getStatusCodeValue());

        String responseMessage = ((com.example.Project_Online_market.response.ApiResponses) response.getBody()).getMessage();
        assertEquals("Order not found", responseMessage);

        verify(ordersRepository, times(0)).deleteById(1);
    }

    @Test
    void testGetOrderById_Success() {

        when(ordersRepository.findById(1)).thenReturn(Optional.of(order));
        ResponseEntity<?> response = ordersController.getOrderById(1);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ApiResponses);
        ApiResponses apiResponse = (ApiResponses) response.getBody();
        assertEquals("Order retrieved successfully", apiResponse.getMessage()); 
        assertNotNull(apiResponse.getData());

        verify(ordersRepository, times(1)).findById(1);
    }

        @Test
        void testGetorderById_NotFound() {

            when(ordersRepository.findById(99)).thenReturn(Optional.empty());
            ResponseEntity<?> response = ordersController.getOrderById(99);

            assertEquals(404, response.getStatusCodeValue()); 
            assertTrue(response.getBody() instanceof ApiResponses); 
            ApiResponses apiResponse = (ApiResponses) response.getBody();
            assertEquals("Order not found", apiResponse.getMessage());
            assertNull(apiResponse.getData());
            verify(ordersRepository, times(1)).findById(99);
        }

}
