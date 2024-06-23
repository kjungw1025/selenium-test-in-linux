package com.example.demo.domain.product.service;

import com.example.demo.domain.product.model.dto.list.SummarizedProductDto;
import com.example.demo.domain.product.model.dto.response.ResponseSingleProductDto;
import com.example.demo.domain.product.model.entity.Product;
import com.example.demo.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public Page<SummarizedProductDto> list(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);

        return products.map(SummarizedProductDto::new);
    }

    public ResponseSingleProductDto findOne(Long id) {
        Product product = productRepository.findOne(id);
        return new ResponseSingleProductDto(product);
    }
}
