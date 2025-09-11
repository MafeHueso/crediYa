package co.com.pragma.model.loanapplication;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
@Builder
public record LoanApplication (
 Long applicationId,
 Long identificationNumber,
 BigDecimal amount,
 Integer termMonths,
 Integer   statusId,
 Long loanTypeId,
 OffsetDateTime applicationDate
){}
