package com.example.demo.domain.product.controller;

import com.example.demo.domain.product.service.CrawlingProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final CrawlingProductService crawlingProductService;

    @GetMapping("/download/excel")
    public void downloadExcelTest() {
        crawlingProductService.crawlProduct();
    }
}
