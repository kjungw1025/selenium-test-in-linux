package com.example.demo.domain.product.service;

import com.example.demo.domain.product.model.entity.Product;
import com.example.demo.domain.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParsingExcelService {

    @Value("${file.excel.path}")
    private String fileDownloadPath;

    @Value("${file.krx.path}")
    private String krxPath;

    private final ProductRepository productRepository;

    private final ParsingProspectusService parsingProspectusService;

    private final List<String> publishers = List.of(
            "신한", "KB", "kb", "한화", "삼성", "미래에셋", "유안타", "키움",
                "교보", "NH", "SK", "대신", "메리츠", "하나",
                "현대차", "한국투자", "트루", "대신", "신영", "유진",
                "하이투자", "비엔케이", "BNK", "IBK", "DB"
            );

    @Transactional
    public void parsingExcel() throws IOException, InvalidFormatException {

        File file = new File(fileDownloadPath);

        if (!file.exists()) {
            log.error("File does not exist");
            return;
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            Workbook workbook = null;
            Row row = null;

            // 엑셀 97 ~ 2003 까지는 HSSF(xls), 엑셀 2007 이상은 XSSF(xlsx)
            if (fileDownloadPath.endsWith(".xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else if (fileDownloadPath.endsWith(".xlsx")) {
                try (OPCPackage opcPackage = OPCPackage.open(inputStream)) {
                    workbook = new XSSFWorkbook(opcPackage);
                }
            } else {
                log.error("Unsupported Excel file format");
            }

            // 엑셀 파일을 처리하는 로직을 여기에 작성
            // 예: sheet, row, cell 탐색 및 데이터 처리

            // 엑셀 파일에서 첫 번째 시트 불러오기
            Sheet sheet = workbook.getSheetAt(0);

            int rows = sheet.getPhysicalNumberOfRows();
            log.info("엑셀 행 개수 " + rows);

            for (int r = 1; r < rows - 140; r++) {
                row = sheet.getRow(r);

                Cell bCell = row.getCell(1);    // 발행 회사
                Cell cCell = row.getCell(2);    // 신용등급
                Cell dCell = row.getCell(3);    // 상품명
                Cell eCell = row.getCell(4);    // 기초자산
                Cell fCell = row.getCell(5);    // 발행일
                Cell gCell = row.getCell(6);    // 만기일
                Cell hCell = row.getCell(7);    // 조건 충족 시 수익률(연, %)
                Cell iCell = row.getCell(8);    // 최대손실률(%)
                Cell jCell = row.getCell(9);    // 청약시작일
                Cell kCell = row.getCell(10);   // 청약종료일
                Cell lCell = row.getCell(11);   // 상품유형
                Cell nCell = row.getCell(13);   // 홈페이지
                Cell pCell = row.getCell(15);   // 비고

                // 이미 존재하면 패스
                if (productRepository.findProductByName(dCell.getStringCellValue()).isPresent())    continue;

                if (findProspectusLink(findProductSession(dCell.getStringCellValue()), findPublisher(dCell.getStringCellValue())) != null) {
                    Product product = Product.builder()
                            .publisher(bCell.getStringCellValue())
                            .name(dCell.getStringCellValue())
                            .equities(eCell.getStringCellValue().replace("<br/>", ", "))
                            .issuedDate(convertToLocalDate(fCell.getStringCellValue()))
                            .maturityDate(convertToLocalDate(gCell.getStringCellValue()))
                            .yieldIfConditionsMet(BigDecimal.valueOf(hCell.getNumericCellValue()))
                            .maximumLossRate(BigDecimal.valueOf(iCell.getNumericCellValue()))
                            .subscriptionStartDate(convertToLocalDate(jCell.getStringCellValue()))
                            .subscriptionEndDate(convertToLocalDate(kCell.getStringCellValue()))
                            .type(lCell.getStringCellValue())
                            .link(nCell.getStringCellValue())
                            .remarks(pCell.getStringCellValue())
                            .summaryInvestmentProspectusLink(findProspectusLink(findProductSession(dCell.getStringCellValue()), findPublisher(dCell.getStringCellValue())))
                            .earlyRepaymentEvaluationDates(String.join(
                                    ", ",
                                    parsingProspectusService.findEarlyRepaymentEvaluationDates(
                                        findProductSession(dCell.getStringCellValue()),
                                        findProspectusLink(findProductSession(dCell.getStringCellValue()), findPublisher(dCell.getStringCellValue()))
                                    )
                                )
                            )
                            .ready(true)
                            .build();
                    productRepository.save(product);
                } else {
                    Product product = Product.builder()
                            .publisher(bCell.getStringCellValue())
                            .name(dCell.getStringCellValue())
                            .equities(eCell.getStringCellValue().replace("<br/>", ", "))
                            .issuedDate(convertToLocalDate(fCell.getStringCellValue()))
                            .maturityDate(convertToLocalDate(gCell.getStringCellValue()))
                            .yieldIfConditionsMet(BigDecimal.valueOf(hCell.getNumericCellValue()))
                            .maximumLossRate(BigDecimal.valueOf(iCell.getNumericCellValue()))
                            .subscriptionStartDate(convertToLocalDate(jCell.getStringCellValue()))
                            .subscriptionEndDate(convertToLocalDate(kCell.getStringCellValue()))
                            .type(lCell.getStringCellValue())
                            .link(nCell.getStringCellValue())
                            .remarks(pCell.getStringCellValue())
                            .ready(false)
                            .build();
                    productRepository.save(product);
                }
            }

        } catch (IOException | InvalidFormatException e) {
            log.error("Error processing Excel file: ", e);
        }

    }

    private String findProductSession(String name) {

        String number = null;

        /**
         * 정규식 패턴을 정의
         *
         * 숫자 앞에 공백 문자, "제", "회", "호" 또는 문자열의 시작이 있어야 하고,
         * 숫자 뒤에 공백 문자, "제", "회", "호" 또는 문자열의 끝이 있어야 함
         */
        String regex = "(?<=\\s|제|회|호|^)\\d+(?=\\s|제|회|호|$)";

        // 정규식 패턴을 컴파일
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);

        // 패턴에 매칭되는 회차(숫자)를 찾기
        while (matcher.find()) {
            number = matcher.group();
        }

        return number;
    }

    private String findPublisher(String name) {

        // 단어가 포함되어 있는지 확인하고, 해당 단어를 반환
        Optional<String> result = publishers.stream()
                .filter(name::contains)
                .findFirst();

        return result.orElseThrow();

    }

    private String findProspectusLink(String session, String publisher) {

        String prospectusLink = null;

        if (session != null) {
            // JSON 파일을 읽고 처리하기 위한 ObjectMapper 생성
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                // JSON 파일을 JsonNode 배열로 변환
                JsonNode jsonArray = objectMapper.readTree(new File(krxPath));

                // "ISU_NM" 키가 session 문자열을 포함하고 publisher 문자열도 포함하는 항목을 찾기
                for (JsonNode jsonNode : jsonArray) {
                    String isuNm = jsonNode.get("ISU_NM").asText();
                    if (isuNm.contains(session) && isuNm.contains(publisher)) {
                        prospectusLink = jsonNode.get("ISU_DISCLS_URL").asText();
                        break;
                    }
                }

            } catch (IOException e) {
                log.error("Error processing json file: ", e);
            }
        }

        return prospectusLink;
    }

    private LocalDate convertToLocalDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDate.parse(dateString, formatter);
    }
}
