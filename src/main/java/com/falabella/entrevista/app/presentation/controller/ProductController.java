package com.falabella.entrevista.app.presentation.controller;

import com.falabella.entrevista.app.data.models.Product;
import com.falabella.entrevista.app.domain.dto.ProductDto;
import com.falabella.entrevista.app.domain.services.impl.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService entityService;

    @GetMapping
    public ResponseEntity<List<Product>> findAll(){
        log.info("CALLING ENDPOINT FIND ALL: GET /api/v1");
        return entityService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id){
        log.info("CALLING ENDPOINT FIND BY ID: GET /api/v1/{}", id);
        return entityService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody ProductDto productDto){
        log.info("CALLING ENDPOINT CREATE: POST /api/v1");
        return entityService.save(productDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable String id, @RequestBody ProductDto body){
        log.info("CALLING ENDPOINT UPDATE: PUT /api/v1/{}", id);
        return entityService.update(id, body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id){
        log.info("CALLING ENDPOINT DELETE: DELETE /api/v1/{}", id);
        return entityService.deleteById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> partialUpdate(@PathVariable String id, @RequestBody ProductDto productDto) throws JsonProcessingException {
        log.info("CALLING ENDPOINT PARTIAL UPDATE: PATCH /api/v1/{}", id);
        return entityService.partialUpdate(id, productDto);
    }
}
