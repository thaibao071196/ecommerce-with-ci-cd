package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemControllerTest {
    private ItemRepository itemRepository;
    private ItemController itemController;

    @Before
    public void setup() {
        itemRepository = Mockito.mock(ItemRepository.class);
        itemController = new ItemController(itemRepository);

        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setPrice(BigDecimal.valueOf(3.0));
        item.setDescription("This is Item 1");

        List<Item> items = new ArrayList<>();
        items.add(item);

        List<Item> emptyItems = new ArrayList<>();

        Mockito.when(itemRepository.findByName("Item 1")).thenReturn(items);
        Mockito.when(itemRepository.findByName("Item 3")).thenReturn(emptyItems);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.findAll()).thenReturn(items);
    }

    @Test
    public void getItemByIdSuccess() {
        ResponseEntity<Item> item = itemController.getItemById(1L);
        Assert.assertEquals(200, item.getStatusCodeValue());
    }

    @Test
    public void getItemByIdFailure() {
        ResponseEntity<Item> item = itemController.getItemById(2L);
        Assert.assertEquals(404, item.getStatusCodeValue());
    }

    @Test
    public void getItemByNameSuccess() {
        ResponseEntity<List<Item>> items = itemController.getItemsByName("Item 1");
        Assert.assertEquals(200, items.getStatusCodeValue());
    }

    @Test
    public void getItemByNameFailureWithItemNotFound() {
        ResponseEntity<List<Item>> items = itemController.getItemsByName("Item 2");
        Assert.assertEquals(404, items.getStatusCodeValue());
    }

    @Test
    public void getItemByNameFailureWithItemsEmpty() {
        ResponseEntity<List<Item>> items = itemController.getItemsByName("Item 3");
        Assert.assertEquals(404, items.getStatusCodeValue());
    }

    @Test
    public void getItemsSuccess() {
        ResponseEntity<List<Item>> items = itemController.getItems();
        Assert.assertEquals(200, items.getStatusCodeValue());
    }
}

