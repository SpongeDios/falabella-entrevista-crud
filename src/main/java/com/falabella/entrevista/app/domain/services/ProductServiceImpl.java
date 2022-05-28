package com.falabella.entrevista.app.domain.services;

import com.falabella.entrevista.app.data.dao.ProductDao;
import com.falabella.entrevista.app.data.models.Product;
import com.falabella.entrevista.app.domain.dto.ConstraintViolationResponse;
import com.falabella.entrevista.app.domain.dto.ProductDto;
import com.falabella.entrevista.app.domain.dto.ReasonResponse;
import com.falabella.entrevista.app.domain.services.impl.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

import javax.validation.*;
import java.net.URI;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService{
    private static final String NOT_FOUND_MESSAGE = "Entity with Id %s not found";
    private static final String PERSISTENCE_ERROR_MESSAGE = "Error intentando guardar la entidad";
    private static final String DEFAULT_ERROR_MESSAGE = "Error en el campo '%s': %s";

    private final ProductDao productDao;

    @Override
    public ResponseEntity<List<Product>> findAll(){
        List<Product> allProducts = productDao.findByStatusTrue();
        if (allProducts.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(allProducts);
    }

    @Override
    public ResponseEntity<Object> findById(String id) {
        Optional<Product> entidadOpcional = productDao.findBySkuAndStatusTrue(id);
        if (entidadOpcional.isEmpty()) {
            log.error("ERROR TRYING TO FIND PRODUCT WITH ID: {}", id);
            return ResponseEntity.badRequest().body(new ReasonResponse(String.format(NOT_FOUND_MESSAGE, id)));
        }
        log.info("PRODUCT SUCCESSFULLY FOUND");
        return ResponseEntity.ok(entidadOpcional.get());
    }

    @Override
    public ResponseEntity<Object> save(ProductDto productDto){
        Product product = Product.builder()
                .name(productDto.getName())
                .brand(productDto.getBrand())
                .size(productDto.getSize())
                .price(productDto.getPrice())
                .principalImage(productDto.getPrincipalImage())
                .otherImages(productDto.getOtherImages())
                .status(Boolean.TRUE)
                .build();

        return sendProductToPersist(product);
    }

    @Override
    public ResponseEntity<Object> update(String id, ProductDto product) {
        Optional<Product> productOptional = productDao.findBySkuAndStatusTrue(id);
        if (productOptional.isEmpty()) {
            log.error("ERROR TRYING TO FIND PRODUCT WITH ID: {}", id);
            return ResponseEntity.badRequest().body(new ReasonResponse(String.format(NOT_FOUND_MESSAGE, id)));
        }
        Product productDB = productOptional.get();
        productDB.setName(product.getName());
        productDB.setBrand(product.getBrand());
        productDB.setSize(product.getSize());
        productDB.setPrice(product.getPrice());
        productDB.setPrincipalImage(product.getPrincipalImage());
        productDB.setOtherImages(product.getOtherImages());

        return sendProductToPersist(productDB);
    }

    @Override
    public ResponseEntity<Object> deleteById(String id) {
        Optional<Product> productOptional = productDao.findBySkuAndStatusTrue(id);
        if (productOptional.isEmpty()) {
            log.error("ERROR TRYING TO FIND PRODUCT WITH ID: {}", id);
            return ResponseEntity.badRequest().body(new ReasonResponse(String.format(NOT_FOUND_MESSAGE, id)));
        }
        Product product = productOptional.get();
        product.setStatus(Boolean.FALSE);
        productDao.save(product);
        log.info("PRODUCTO WITH ID: {} SUCCESSFULLY REMOVED", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<Object> partialUpdate(String id, ProductDto productDto) {
        Optional<Product> productOptional = productDao.findBySkuAndStatusTrue(id);
        if (productOptional.isEmpty()) {
            log.error("ERROR TRYING TO FIND PRODUCT WITH ID: {}", id);
            return ResponseEntity.badRequest().body(new ReasonResponse(String.format(NOT_FOUND_MESSAGE, id)));
        }
        Product productDB = productOptional.get();
        updateNotNullProperties(productDB, productDto);
        return sendProductToPersist(productDB);
    }

    private ResponseEntity<Object> sendProductToPersist(Product product){
        log.info("TRYING TO SAVE THE PRODUCT");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Product>> constraintViolations = validator.validate(product);

        if (!constraintViolations.isEmpty()){
            log.error("ERROR WHILE TRYING TO PERSIST THE PRODUCT");
            List<String> errorWithMessage = new ArrayList<>();
            constraintViolations.forEach(error -> {
                errorWithMessage.add(String.format(DEFAULT_ERROR_MESSAGE, error.getPropertyPath(), error.getMessage()));
            });
            ConstraintViolationResponse constraintViolationResponse = ConstraintViolationResponse.builder()
                    .message(PERSISTENCE_ERROR_MESSAGE)
                    .errors(errorWithMessage)
                    .build();
            return ResponseEntity.badRequest().body(constraintViolationResponse);
        }
        Product productDB = productDao.save(product);
        log.info("PRODUCT SUCCESSFULLY STORED");
        return ResponseEntity.created(URI.create("/api/v1")).body(productDB);
    }

    private void updateNotNullProperties(Product productDB, ProductDto productDto){
        log.info("UPDATING NON NULL PROPERTIES");
        String name = productDto.getName();
        String brand = productDto.getBrand();
        String size = productDto.getSize();
        Double price = productDto.getPrice();
        String principalImage = productDto.getPrincipalImage();
        List<String> otherImages = productDto.getOtherImages();

        if (!Objects.isNull(name))
            productDB.setName(name);

        if (!Objects.isNull(brand))
            productDB.setBrand(brand);

        if (!Objects.isNull(size))
            productDB.setSize(size);

        if (!Objects.isNull(price))
            productDB.setPrice(price);

        if (!Objects.isNull(principalImage))
            productDB.setPrincipalImage(principalImage);

        if (!Objects.isNull(otherImages))
            productDB.setOtherImages(otherImages);
    }


}
