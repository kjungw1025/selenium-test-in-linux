package com.example.demo.domain.product.model.dto.response;

import com.example.demo.domain.product.model.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class ResponseSingleProductDto {

    @Schema(description = "상품 id", example = "1")
    private final Long id;

    @Schema(description = "상품명", example = "oo투자증권 99999")
    private final String name;

    @Schema(description = "기초자산", example = "KOSPI200 Index, HSCEI Index, S&P500 Index")
    private final String equities;

    @Schema(description = "발행일", example = "2024-06-21")
    private final LocalDate issuedDate;

    @Schema(description = "만기일", example = "2027-06-21")
    private final LocalDate maturityDate;

    @Schema(description = "조건 충족시 수익률(연, %)", example = "10.2")
    private final BigDecimal yieldIfConditionsMet;

    @Schema(description = "최대손실률(%)", example = "-100")
    private final BigDecimal maximumLossRate;

    @Schema(description = "청약 시작일", example = "2024-06-14")
    private final LocalDate subscriptionStartDate;

    @Schema(description = "청약 마감일", example = "2024-06-21")
    private final LocalDate subscriptionEndDate;

    @Schema(description = "상품유형", example = "[월지급식]...")
    private final String type;

    @Schema(description = "홈페이지 링크", example = "https://...")
    private final String link;

    @Schema(description = "비고", example = "2024.06.21 13:00 청약종료")
    private final String remarks;

    @Schema(description = "간의투자설명서 링크", example = "https://...")
    private final String summaryInvestmentProspectusLink;

    @Schema(description = "자동조기상환평가일", example = "1차: 2024년 12월 20일, 2차: 2025년 06월 20일, 3차: 2025년 12월 19일")
    private final String earlyRepaymentEvaluationDates;

    public ResponseSingleProductDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.equities = product.getEquities();
        this.issuedDate = product.getIssuedDate();
        this.maturityDate = product.getMaturityDate();
        this.yieldIfConditionsMet = product.getYieldIfConditionsMet();
        this.maximumLossRate = product.getMaximumLossRate();
        this.subscriptionStartDate = product.getSubscriptionStartDate();
        this.subscriptionEndDate = product.getSubscriptionEndDate();
        this.type = product.getType();
        this.link = product.getLink();
        this.remarks = product.getRemarks();
        this.summaryInvestmentProspectusLink = product.getSummaryInvestmentProspectusLink();
        this.earlyRepaymentEvaluationDates = product.getEarlyRepaymentEvaluationDates();
    }
}
