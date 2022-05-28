package com.falabella.entrevista.app.presentation.controller;

import com.falabella.entrevista.app.data.models.Product;
import com.falabella.entrevista.app.domain.dto.ProductDto;
import com.falabella.entrevista.app.domain.services.impl.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {
    private static final String id = "any-id";

    private ProductService productService;
    private ProductController productController;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        productController = new ProductController(productService);
    }

    @Test
    void test_findAll_given_a_valid_request_will_return_ok() {
        //arrange
        when(productService.findAll()).thenReturn(ResponseEntity.ok(Collections.singletonList(new Product())));

        //act
        ResponseEntity<List<Product>> response = productController.findAll();

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_findById_given_a_valid_id_will_return_ok() {
        //arrange
        when(productService.findById(anyString())).thenReturn(ResponseEntity.ok(new Product()));

        //act
        ResponseEntity<Object> response = productController.findById(id);

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_save_given_a_valid_productDto_will_return_ok() {
        //arrange
        when(productService.save(any(ProductDto.class))).thenReturn(ResponseEntity.ok(new Product()));

        //act
        ResponseEntity<Object> response = productController.create(new ProductDto());

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_update_given_a_valid_id_and_valid_productDto_will_return_ok() {
        //arrange
        when(productService.update(anyString(), any(ProductDto.class))).thenReturn(ResponseEntity.created(URI.create("/api/v1")).body(new Product()));

        //act
        ResponseEntity<Object> response = productController.update(id, new ProductDto());

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void test_delete_given_a_valid_id_will_return_204() {
        //arrange
        when(productService.deleteById(anyString())).thenReturn(ResponseEntity.noContent().build());

        //act
        ResponseEntity<Object> response = productController.delete(id);

        //asserts
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void test_partialUpdate_given_a_valid_id_and_productDto_will_return_create() throws JsonProcessingException {
        //arrange
        when(productService.partialUpdate(anyString(), any(ProductDto.class))).thenReturn(ResponseEntity.created(URI.create("/api/v1")).body(new Product()));

        //act
        ResponseEntity<Object> response = productController.partialUpdate(id, new ProductDto());

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}