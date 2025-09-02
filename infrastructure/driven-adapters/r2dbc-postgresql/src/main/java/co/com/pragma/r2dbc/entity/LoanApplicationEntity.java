package co.com.pragma.r2dbc.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Table("loan_applications")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoanApplicationEntity {

    @Id
    @Column(name = "application_id")
    private Long applicationId;

    @Column(name ="identification_number")
    private String identificationNumber;
    @Column(name ="amount")
    private BigDecimal amount;
    @Column(name ="term_months")
    private Integer termMonths;
    @Column(name ="status_id")
    private Integer   statusId;
    @Column(name ="loan_type_id")
    private Long loanTypeId;
    @Column(name ="application_date")
    private OffsetDateTime applicationDate;
}
