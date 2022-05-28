package com.falabella.entrevista.app.data.dao;

import com.falabella.entrevista.app.data.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDao extends JpaRepository<Product, String> {
    List<Product> findByStatusTrue();
    Optional<Product> findBySkuAndStatusTrue(String id);
}
