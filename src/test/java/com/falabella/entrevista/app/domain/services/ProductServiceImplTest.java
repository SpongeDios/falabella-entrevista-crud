package com.falabella.entrevista.app.domain.services;

import com.falabella.entrevista.app.data.dao.ProductDao;
import com.falabella.entrevista.app.data.models.Product;
import com.falabella.entrevista.app.domain.dto.ConstraintViolationResponse;
import com.falabella.entrevista.app.domain.dto.ProductDto;
import com.falabella.entrevista.app.domain.dto.ReasonResponse;
import com.falabella.entrevista.app.domain.services.impl.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {
    private static final String ANY_NAME = "any-name";
    private static final String ANY_BRAND = "any-brand";
    private static final String ANY_SIZE = "any-size";
    private static final Double ANY_PRICE = 10.00;
    private static final String ANY_URL_IMAGE = "https://any-url.net";
    private static final List<String> ANY_OTHER_IMAGES = Arrays.asList("https://any-url.net", "https://any-url2.net", "https://any-url3.net");
    private static final String id = "any-id";
    private static final String MAX_SIZE_VALUE_PARAMETERS = "NAMENAMENAMENAMENAMENAMENAMENAMENAMENAMENAMENAMENAM, BRANDBRANDBRANDBRANDBRANDBRANDBRANDBRANDBRANDBRANDB, 9999999999.00";


    private ProductDao productDao;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productDao = mock(ProductDao.class);
        productService = new ProductServiceImpl(productDao);
    }

    @Test
    void test_findAll_will_return_ok() {
        //arrange
        when(productDao.findByStatusTrue()).thenReturn(Collections.singletonList(new Product()));

        //act
        ResponseEntity<List<Product>> response = productService.findAll();

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_findAll_without_products_will_return_204() {
        //arrange
        when(productDao.findByStatusTrue()).thenReturn(Collections.emptyList());

        //act
        ResponseEntity<List<Product>> response = productService.findAll();

        //asserts
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }


    @Test
    void test_findById_given_a_invalid_sku_product_will_return_400() {
        //arrange
        when(productDao.findBySkuAndStatusTrue(id)).thenReturn(Optional.empty());

        //act
        ResponseEntity<Object> response = productService.findById(id);

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(((ReasonResponse) response.getBody()).getErrorReason());
        assertEquals("Entity with Id any-id not found", ((ReasonResponse) response.getBody()).getErrorReason());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void test_findById_given_a_valid_sku_product_will_return_ok() {
        //arrange
        when(productDao.findBySkuAndStatusTrue(id)).thenReturn(Optional.of(Product.builder()
                .name(ANY_NAME)
                .brand(ANY_BRAND)
                .size(ANY_SIZE)
                .price(ANY_PRICE)
                .principalImage(ANY_URL_IMAGE)
                .otherImages(ANY_OTHER_IMAGES)
                .build()));
        //act
        ResponseEntity<Object> response = productService.findById(id);

        //assets
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_save_given_a_valid_productDto_with_all_properties_will_return_ok() {
        //arrange
        when(productDao.save(any(Product.class))).thenReturn(new Product());
        ProductDto productDto = ProductDto.builder()
                .name(ANY_NAME)
                .brand(ANY_BRAND)
                .size(ANY_SIZE)
                .price(ANY_PRICE)
                .principalImage(ANY_URL_IMAGE)
                .otherImages(ANY_OTHER_IMAGES)
                .build();
        //act
        ResponseEntity<Object> response = productService.save(productDto);

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @ParameterizedTest
    @CsvSource({
            ",,,,"
    })
    void test_save_given_a_productDto_with_all_null_properties_will_return_400(String name, String brand, String size, Double price, String principalImages) {
        //arrange
        ProductDto productDto = ProductDto.builder()
                .name(name)
                .brand(brand)
                .size(size)
                .price(price)
                .principalImage(principalImages)
                .otherImages(ANY_OTHER_IMAGES)
                .build();
        //act
        ResponseEntity<Object> response = productService.save(productDto);

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(((ConstraintViolationResponse)response.getBody()).getMessage());
        assertNotNull(((ConstraintViolationResponse)response.getBody()).getErrors());
        assertEquals(7, ((ConstraintViolationResponse)response.getBody()).getErrors().size());
        assertEquals("Error intentando guardar la entidad", ((ConstraintViolationResponse)response.getBody()).getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @CsvSource({
            "n,b,0.99"
    })

    void test_save_given_a_productDto_with_min_size_and_value_will_return_400(String name, String brand, Double price) {
        //arrange
        ProductDto productDto = ProductDto.builder()
                .name(name)
                .brand(brand)
                .size(ANY_SIZE)
                .price(price)
                .principalImage(ANY_URL_IMAGE)
                .otherImages(ANY_OTHER_IMAGES)
                .build();
        //act
        ResponseEntity<Object> response = productService.save(productDto);

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(((ConstraintViolationResponse)response.getBody()).getMessage());
        assertNotNull(((ConstraintViolationResponse)response.getBody()).getErrors());
        assertEquals(3, ((ConstraintViolationResponse)response.getBody()).getErrors().size());
        assertEquals("Error intentando guardar la entidad", ((ConstraintViolationResponse)response.getBody()).getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @CsvSource({MAX_SIZE_VALUE_PARAMETERS})
    void test_save_given_a_productDto_with_max_size_and_value_will_return_400(String name, String brand, Double price) {
        //arrange
        ProductDto productDto = ProductDto.builder()
                .name(name)
                .brand(brand)
                .size(ANY_SIZE)
                .price(price)
                .principalImage(ANY_URL_IMAGE)
                .otherImages(ANY_OTHER_IMAGES)
                .build();
        //act
        ResponseEntity<Object> response = productService.save(productDto);

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(((ConstraintViolationResponse)response.getBody()).getMessage());
        assertNotNull(((ConstraintViolationResponse)response.getBody()).getErrors());
        assertEquals(3, ((ConstraintViolationResponse)response.getBody()).getErrors().size());
        assertEquals("Error intentando guardar la entidad", ((ConstraintViolationResponse)response.getBody()).getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void test_delete_given_a_valid_sku_will_return_204() {
        //arrange
        when(productDao.findBySkuAndStatusTrue(anyString())).thenReturn(Optional.of(new Product()));

        //act
        ResponseEntity<Object> response = productService.deleteById(id);

        //asserts
        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void test_delete_given_a_invalid_sku_will_return_400() {
        //arrange
        when(productDao.findBySkuAndStatusTrue(anyString())).thenReturn(Optional.empty());

        //act
        ResponseEntity<Object> response = productService.deleteById(id);

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(((ReasonResponse) response.getBody()).getErrorReason());
        assertEquals("Entity with Id any-id not found" ,((ReasonResponse) response.getBody()).getErrorReason());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void test_update_given_a_valid_productDto_and_valid_id_will_return_201() {
        //arrange
        when(productDao.findBySkuAndStatusTrue(anyString())).thenReturn(Optional.of(new Product()));
        when(productDao.save(any(Product.class))).thenReturn(new Product());
        ProductDto productDto = ProductDto.builder()
                .name(ANY_NAME)
                .brand(ANY_BRAND)
                .size(ANY_SIZE)
                .price(ANY_PRICE)
                .principalImage(ANY_URL_IMAGE)
                .otherImages(ANY_OTHER_IMAGES)
                .build();
        //act
        ResponseEntity<Object> response = productService.update(id, productDto);

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void test_update_given_a_valid_productDto_and_invalid_id_will_return_400() {
        //arrange
        when(productDao.findBySkuAndStatusTrue(anyString())).thenReturn(Optional.empty());

        //act
        ResponseEntity<Object> response = productService.update(id, new ProductDto());

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(((ReasonResponse) response.getBody()).getErrorReason());
        assertEquals("Entity with Id any-id not found" ,((ReasonResponse) response.getBody()).getErrorReason());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @CsvSource({",,,,"})
    void test_update_given_a_valid_id_and_productDto_with_all_null_properties_will_return_400(String name, String brand, String size, Double price, String principalImages) {
        //arrange
        when(productDao.findBySkuAndStatusTrue(anyString())).thenReturn(Optional.of(new Product()));
        ProductDto productDto = ProductDto.builder()
                .name(name)
                .brand(brand)
                .size(size)
                .price(price)
                .principalImage(principalImages)
                .otherImages(ANY_OTHER_IMAGES)
                .build();
        //act
        ResponseEntity<Object> response = productService.update(id, productDto);

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(((ConstraintViolationResponse)response.getBody()).getMessage());
        assertNotNull(((ConstraintViolationResponse)response.getBody()).getErrors());
        assertEquals(7, ((ConstraintViolationResponse)response.getBody()).getErrors().size());
        assertEquals("Error intentando guardar la entidad", ((ConstraintViolationResponse)response.getBody()).getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @CsvSource({"n,b,0.99"})
    void test_update_given_a_valid_id_and_productDto_with_min_size_and_value_will_return_400(String name, String brand, Double price) {
        //arrange
        when(productDao.findBySkuAndStatusTrue(anyString())).thenReturn(Optional.of(new Product()));
        ProductDto productDto = ProductDto.builder()
                .name(name)
                .brand(brand)
                .size(ANY_SIZE)
                .price(price)
                .principalImage(ANY_URL_IMAGE)
                .otherImages(ANY_OTHER_IMAGES)
                .build();
        //act
        ResponseEntity<Object> response = productService.update(id, productDto);

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(((ConstraintViolationResponse)response.getBody()).getMessage());
        assertNotNull(((ConstraintViolationResponse)response.getBody()).getErrors());
        assertEquals(3, ((ConstraintViolationResponse)response.getBody()).getErrors().size());
        assertEquals("Error intentando guardar la entidad", ((ConstraintViolationResponse)response.getBody()).getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @CsvSource({MAX_SIZE_VALUE_PARAMETERS})
    void test_update_given_a_valid_id_and_productDto_with_max_size_and_value_will_return_400(String name, String brand, Double price) {
        //arrange
        when(productDao.findBySkuAndStatusTrue(anyString())).thenReturn(Optional.of(new Product()));
        ProductDto productDto = ProductDto.builder()
                .name(name)
                .brand(brand)
                .size(ANY_SIZE)
                .price(price)
                .principalImage(ANY_URL_IMAGE)
                .otherImages(ANY_OTHER_IMAGES)
                .build();
        //act
        ResponseEntity<Object> response = productService.update(id, productDto);

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(((ConstraintViolationResponse)response.getBody()).getMessage());
        assertNotNull(((ConstraintViolationResponse)response.getBody()).getErrors());
        assertEquals(3, ((ConstraintViolationResponse)response.getBody()).getErrors().size());
        assertEquals("Error intentando guardar la entidad", ((ConstraintViolationResponse)response.getBody()).getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @CsvSource({
            "nike,,,",
            ",nike,,",
            ",,10.00,",
            ",,,https://any-url.com"
    })
    void test_partialUpdate_given_a_valid_id_and_product_will_return_201(String name, String brand, Double price, String principalImages ) throws JsonProcessingException {
        //arrange
        Product productDB = Product.builder()
                .sku("ANY_SKU")
                .name(ANY_NAME)
                .brand(ANY_BRAND)
                .size(ANY_SIZE)
                .price(ANY_PRICE)
                .principalImage(ANY_URL_IMAGE)
                .otherImages(ANY_OTHER_IMAGES)
                .build();

        ProductDto productDto = ProductDto.builder()
                .name(name)
                .brand(brand)
                .price(price)
                .size(ANY_SIZE)
                .principalImage(ANY_URL_IMAGE)
                .otherImages(ANY_OTHER_IMAGES)
                .build();


        when(productDao.findBySkuAndStatusTrue(anyString())).thenReturn(Optional.of(productDB));
        when(productDao.save(any(Product.class))).thenReturn(new Product());

        //act
        ResponseEntity<Object> response = productService.partialUpdate(id, productDto);

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void test_partialupdate_given_a_invalid_id_will_return_400() throws JsonProcessingException {
        //arrange
        when(productDao.findBySkuAndStatusTrue(anyString())).thenReturn(Optional.empty());

        //act
        ResponseEntity<Object> response = productService.partialUpdate(id, new ProductDto());

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(((ReasonResponse)response.getBody()).getErrorReason());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Entity with Id any-id not found", ((ReasonResponse)response.getBody()).getErrorReason());
    }
}