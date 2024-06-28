package com.example.demo.domain.product.model.entity;

import com.example.demo.domain.product.model.ProductState;
import com.example.demo.global.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "product_id")
    private Long id;

    @NotNull
    private String publisher;

//    @NotNull
//    private String creditRating;

    @NotNull
    private String name;

//    @NotNull
//    private int equityCount;

    @NotNull
    private String equities;

    @NotNull
    private LocalDate issuedDate;

    @NotNull
    private LocalDate maturityDate;

    @NotNull
    @Column(precision = 8, scale = 5)
    private BigDecimal yieldIfConditionsMet;

    @NotNull
    @Column(precision = 8, scale = 5)
    private BigDecimal maximumLossRate;

    @NotNull
    private LocalDate subscriptionStartDate;

    @NotNull
    private LocalDate subscriptionEndDate;

    @NotNull
    private String type;

    @NotNull
    private String link;

    @NotNull
    private String remarks;

    private String summaryInvestmentProspectusLink;

    private String earlyRepaymentEvaluationDates;

    @NotNull
    @ColumnDefault("'INACTIVE'")
    @Enumerated(EnumType.STRING)
    private ProductState productState;

    @Builder
    private Product (@NonNull String publisher,
//                     @NonNull String creditRating,
                     @NonNull String name,
                     @NonNull String equities,
                     @NonNull LocalDate issuedDate,
                     @NonNull LocalDate maturityDate,
                     @NonNull BigDecimal yieldIfConditionsMet,
                     @NonNull BigDecimal maximumLossRate,
                     @NonNull LocalDate subscriptionStartDate,
                     @NonNull LocalDate subscriptionEndDate,
                     @NonNull String type,
                     @NonNull String link,
                     @NonNull String remarks,
                     String summaryInvestmentProspectusLink,
                     String earlyRepaymentEvaluationDates,
                     ProductState productState) {
        this.publisher = publisher;
//        this.creditRating = creditRating;
        this.name = name;
        this.equities = equities;
        this.issuedDate = issuedDate;
        this.maturityDate = maturityDate;
        this.yieldIfConditionsMet = yieldIfConditionsMet;
        this.maximumLossRate = maximumLossRate;
        this.subscriptionStartDate = subscriptionStartDate;
        this.subscriptionEndDate = subscriptionEndDate;
        this.type = type;
        this.link = link;
        this.remarks = remarks;
        this.summaryInvestmentProspectusLink = summaryInvestmentProspectusLink;
        this.earlyRepaymentEvaluationDates = earlyRepaymentEvaluationDates;
        this.productState = productState;
    }
}
