package co.com.pragma.model.pagination;

import java.math.BigDecimal;

public record LoanJoinPagination(
        String identificationNumber,
        BigDecimal amount,
        Integer termMonths,
        String loanName,
        BigDecimal interestRate,
        String status_name

){}
