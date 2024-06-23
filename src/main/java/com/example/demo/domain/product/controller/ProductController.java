package com.example.demo.domain.product.controller;

import com.example.demo.domain.product.model.dto.list.SummarizedProductDto;
import com.example.demo.domain.product.service.DownloadExcelService;
import com.example.demo.domain.product.service.ParsingExcelService;
import com.example.demo.domain.product.service.ParsingProspectusService;
import com.example.demo.domain.product.service.ProductService;
import com.example.demo.global.model.dto.ResponsePage;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final DownloadExcelService downloadExcelService;
    private final ParsingExcelService parsingExcelService;
    private final ParsingProspectusService parsingProspectusService;
    private final ProductService productService;

    /**
     * 엑셀 파일 다운로드 (프론트 호출 금지)
     */
    @GetMapping("/download/excel")
    public void downloadExcelTest() {
        downloadExcelService.crawlProduct();
    }

    /**
     * 엑셀 파일 파싱 (프론트 호출 금지)
     * @throws IOException
     * @throws InvalidFormatException
     */
    @GetMapping("/parsing/excel")
    public void parsingExcelTest() throws IOException, InvalidFormatException {
        parsingExcelService.parsingExcel();
    }

    /**
     * 모든 상품 조회
     */
    @GetMapping
    public ResponsePage<SummarizedProductDto> list(@ParameterObject Pageable pageable) {
        Page<SummarizedProductDto> result = productService.list(pageable);
        return new ResponsePage<>(result);
    }

    //
//    @GetMapping("/parsing/prospectus")
//    public List<String> parsingProspectusTest(@RequestParam("productSession") String productSession,
//                                                       @RequestParam("url") String url) throws IOException {
//        return parsingProspectusService.findEarlyRepaymentEvaluationDates(productSession, url);
//    }
}
