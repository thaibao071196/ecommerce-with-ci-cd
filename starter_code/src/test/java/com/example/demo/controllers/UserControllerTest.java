package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    UserRepository userRepository;
    CartRepository cartRepository;
    BCryptPasswordEncoder passwordEncoder;
    UserController userController;

    private User user;

    @Before
    public void setup() {
        userRepository = mock(UserRepository.class);
        cartRepository = mock(CartRepository.class);
        passwordEncoder = mock(BCryptPasswordEncoder.class);
        userController = new UserController(userRepository, cartRepository, passwordEncoder);

        user = new User();
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

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("baont13")).thenReturn(user);
        when(passwordEncoder.encode("zxcvbnm123")).thenReturn("123Encode");
    }

    @Test
    public void findUserByIdSuccess() {
        ResponseEntity<User> tempUser = userController.findById(1L);
        assertEquals(200, tempUser.getStatusCodeValue());
    }

    @Test
    public void findUserByIdFailed() {
        ResponseEntity<User> tempUser = userController.findById(2L);
        assertEquals(404, tempUser.getStatusCodeValue());
    }

    @Test
    public void findByUserNameSuccess() {
        ResponseEntity<User> tempUser = userController.findByUserName("baont13");
        assertEquals(user, tempUser.getBody());
    }

    @Test
    public void findByUserNameFailed() {
        ResponseEntity<User> tempUser = userController.findByUserName("trangdth");
        assertEquals(null, tempUser.getBody());
    }

    @Test
    public void createUserSuccess() {
        CreateUserRequest request = new CreateUserRequest("baont13", "zxcvbnm123", "zxcvbnm123");

        ResponseEntity<User> tempUser = userController.createUser(request);

        assertNotNull(tempUser);
        assertEquals("123Encode" ,tempUser.getBody().getPassword());
        assertEquals("baont13" ,tempUser.getBody().getUsername());
        assertEquals(0 ,tempUser.getBody().getId());
    }

    @Test
    public void createUserFailedWithErrorPassword() {
        CreateUserRequest request = new CreateUserRequest("baont13", "zxcvbnm", "zxcvbnm");
        ResponseEntity<User> tempUser = userController.createUser(request);

        assertEquals(400 ,tempUser.getStatusCodeValue());
    }

    @Test
    public void createUserFailedWithWrongConfirmPassword() {
        CreateUserRequest request = new CreateUserRequest("baont13", "zxcvbnm123", "zxcvbnm1234");
        ResponseEntity<User> tempUser = userController.createUser(request);

        assertEquals(400 ,tempUser.getStatusCodeValue());
    }
}
