package co.com.pragma.model.loantype;

import java.math.BigDecimal;

public record LoanType (
        Long loanTypeId,
        String loanName,
        BigDecimal maxAmount,
        BigDecimal minAmount,
        BigDecimal interestRate,
        Boolean automaticValidation
){}
