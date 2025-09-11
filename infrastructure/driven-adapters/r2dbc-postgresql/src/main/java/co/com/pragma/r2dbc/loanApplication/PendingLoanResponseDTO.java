package co.com.pragma.r2dbc.loanApplication;

import java.math.BigDecimal;

public record PendingLoanResponseDTO(
        BigDecimal amount,
        int termMonths,
        String email,
        String name,
        String loanTypeId,
        BigDecimal interestRate,
        String statusId,
        BigDecimal salaryBase
       // BigDecimal totalMonthlyDebtApprovals
) {}