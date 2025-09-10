package co.com.pragma.api.pendingApplicationPagination;

import java.math.BigDecimal;

public record PendingLoanResponseDTO(
        BigDecimal amount,
        int termMonths,
        String email,
        String name,
        String loanTypeId,
        BigDecimal interestRate,
        String statusId,
        BigDecimal salaryBase,
        BigDecimal totalMonthlyDebtApprovals
) {}