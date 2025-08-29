package co.com.pragma.model.loanapplication;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
@Builder
public record LoanApplication (
 Long applicationId,
 String identificationNumber,
 BigDecimal amount,
 Integer termMonths,
 Integer   statusId,
 Long loanTypeId,
 OffsetDateTime applicationDate
){}
