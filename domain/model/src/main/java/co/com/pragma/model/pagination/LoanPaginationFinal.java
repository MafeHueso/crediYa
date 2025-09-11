package co.com.pragma.model.pagination;

import java.math.BigDecimal;

public record LoanPaginationFinal (

        BigDecimal amount,
        int termMonths,
        String email,
        String name,
        String loanTypeId,
        BigDecimal interestRate,
        String statusId,
        BigDecimal salaryBase


){}
