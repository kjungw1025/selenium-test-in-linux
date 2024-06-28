package com.example.demo.domain.product.repository;

import com.example.demo.domain.product.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p where p.productState = 'ACTIVE' order by p.lastModifiedAt DESC ")
    Page<Product> findAll(Pageable pageable);

    @Query("select p from Product p where p.productState = 'ACTIVE' and p.name = :name ")
    Optional<Product> findProductByName(@Param("name") String name);

    @Query("select p from Product p where p.productState = 'ACTIVE' and p.id = :id ")
    Product findOne(@Param("id") Long id);
}
