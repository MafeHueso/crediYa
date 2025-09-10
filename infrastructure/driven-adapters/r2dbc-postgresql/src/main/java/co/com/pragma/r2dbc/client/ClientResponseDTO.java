package co.com.pragma.r2dbc.client;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ClientResponseDTO {
    private BigDecimal amount;
    private int termMonths;
    private String email;
    private String name;
    private String loanTypeId;
    private BigDecimal interestRate;
    private String statusId;
    private String identificationNumber;
    private BigDecimal salaryBase;
    private BigDecimal totalMonthlyDebtApprovals;
}
