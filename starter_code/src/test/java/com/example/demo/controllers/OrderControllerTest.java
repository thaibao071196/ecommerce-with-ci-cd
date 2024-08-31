package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

public class OrderControllerTest {
    private OrderController orderController;
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @Before
    public void setup() {
        orderRepository = Mockito.mock(OrderRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(BCryptPasswordEncoder.class);
        orderController = new OrderController(userRepository, orderRepository);

        User user = new User();
        user.setId(1L);
        user.setUsername("baont13");
        String userPassword = passwordEncoder.encode("zxcvbnm123");
        user.setPassword(userPassword);


        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setPrice(BigDecimal.valueOf(3.0));
        item.setDescription("This is Item 1");
        Cart cart = new Cart();
        cart.addItem(item);
        user.setCart(cart);

        Mockito.when(passwordEncoder.encode("zxcvbnm123")).thenReturn("123Encoder");
        Mockito.when(userRepository.findByUsername("baont13")).thenReturn(user);
    }

    @Test
    public void submitOrderFailureWithUserNotFound() {
        ResponseEntity<UserOrder> userOrder = orderController.submit("trangdth");
        Assert.assertEquals(404, userOrder.getStatusCodeValue());
    }

    @Test
    public void submitOrderSuccess() {
        ResponseEntity<UserOrder> userOrder = orderController.submit("baont13");
        Assert.assertNotNull(userOrder);
        Assert.assertEquals(200, userOrder.getStatusCodeValue());
    }

    @Test
    public void getUserForOrderFailureWithUserNotFound() {
        ResponseEntity<List<UserOrder>> orders = orderController.getOrdersForUser("trangdth");
        Assert.assertEquals(404, orders.getStatusCodeValue());
    }

    @Test
    public void getUserForOrderSuccess() {
        ResponseEntity<List<UserOrder>> orders = orderController.getOrdersForUser("baont13");
        Assert.assertEquals(200, orders.getStatusCodeValue());
    }
}
