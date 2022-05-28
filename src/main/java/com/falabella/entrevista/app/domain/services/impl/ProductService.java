package com.falabella.entrevista.app.domain.services.impl;

import com.falabella.entrevista.app.data.models.Product;
import com.falabella.entrevista.app.domain.dto.ProductDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
    ResponseEntity<List<Product>> findAll();
    ResponseEntity<Object> findById(String id);
    ResponseEntity<Object> save(ProductDto productDto);
    ResponseEntity<Object> update(String id, ProductDto product);
    ResponseEntity<Object> deleteById(String id);
    ResponseEntity<Object> partialUpdate(String id, ProductDto productDto) throws JsonProcessingException;
}
