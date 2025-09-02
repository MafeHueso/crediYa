package co.com.pragma.r2dbc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("loan_type")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoanTypeEntity {

    @Id
    @Column(name = "loan_type_id")
    private Long loanTypeId;

    @Column(name ="loan_name")
    private String loanName;
    @Column(name ="max_amount")
    private BigDecimal maxAmount;
    @Column(name ="min_amount")
    private BigDecimal minAmount;
    @Column(name ="interest_rate")
    private BigDecimal interestRate;
    @Column(name ="automatic_validation")
    private Boolean automaticValidation;
}
