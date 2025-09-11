package co.com.pragma.usecase.loanapplication;

import co.com.pragma.model.loanapplication.LoanStatus;
import co.com.pragma.model.client.UserClientRepository;
import co.com.pragma.model.exception.*;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.LoanApplicationRepository;
import co.com.pragma.model.loantype.LoanTypeRepository;
import co.com.pragma.model.pagination.LoanJoinPagination;
import co.com.pragma.model.pagination.LoanPaginationFinal;
import co.com.pragma.model.pagination.PaginationRequest;
import co.com.pragma.model.pagination.PaginationResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

@RequiredArgsConstructor
public class LoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final UserClientRepository clientRepository;
    private final LoanTypeRepository loanTypeRepository;

    public Mono<String> createLoanApplication(String email, BigDecimal amount, Integer termMonths, Long loanTypeId) {

        if (email == null || email.isEmpty()) {
            return Mono.error(new EmailNotNullException("Field 'email' cannot be null or empty"));
        }

        if (amount == null) {
            return Mono.error(new AmountNotNullException("Field 'amount' cannot be null"));
        }

        if (loanTypeId == null) {
            return Mono.error(new InvalidLoanTypeException("Invalid loan type"));
        }


        if (termMonths == null || termMonths < 6 || termMonths > 60) {
            return Mono.error(new TermLoanException("Loan term must be between 6 and 60 months"));
        }

        return clientRepository.getIdentificationByEmail(email)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found with email: " + email)))
                .flatMap(identification -> {
                    if (identification == null) {
                        return Mono.error(new UserNotFoundException("User not found with email: " + email));
                    }

                    return loanTypeRepository.findByLoanTypeId(loanTypeId)
                            .switchIfEmpty(Mono.error(new InvalidLoanTypeException("Invalid loan type.")))
                            .flatMap(loanType -> {

                                if (amount.compareTo(loanType.minAmount()) < 0 || amount.compareTo(loanType.maxAmount()) > 0) {
                                    return Mono.error(new AmountTypeLoanException(
                                            "Loan amount must be between " + loanType.minAmount() + " and " + loanType.maxAmount()));
                                }

                                return loanApplicationRepository.findByIdentificationNumberAndStatus(identification, 1)
                                        .flatMap(existingLoans -> {
                                            boolean hasPendingLoan = existingLoans.stream()
                                                    .anyMatch(loan -> loan.loanTypeId().equals(loanTypeId));

                                            if (hasPendingLoan) {
                                                return Mono.error(new ExistApplicationTypeLoan("There is already a pending application for this type of loan."));
                                            }

                                            LoanApplication loan = LoanApplication.builder()
                                                    .identificationNumber(identification)
                                                    .amount(amount)
                                                    .termMonths(termMonths)
                                                    .statusId(1)
                                                    .loanTypeId(loanTypeId)
                                                    .applicationDate(OffsetDateTime.now())
                                                    .build();

                                            return loanApplicationRepository.save(loan);
                                        });
                            });
                })
                .map(saved -> "Pending review");
    }

    public Mono<PaginationResponse<LoanPaginationFinal>> getLoanApplications(PaginationRequest paginationRequest, Integer statusId) {
        return loanApplicationRepository.getPendingLoanApplications(paginationRequest, statusId);
    }

    public Mono<BigDecimal> calculateTotalMonthlyDebt(String email) {
        return clientRepository.getIdentificationByEmail(email)
                .flatMap(identification ->
                        loanApplicationRepository.findByIdentificationNumberAndStatus(identification, LoanStatus.APPROVED)
                )
                .flatMapMany(Flux::fromIterable)
                .flatMap(loan ->
                        loanTypeRepository.findByLoanTypeId(loan.loanTypeId())
                                .map(loanType -> {
                                    BigDecimal amount = loan.amount();
                                    int months = loan.termMonths();
                                    BigDecimal interestRate = loanType.interestRate().divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP); // tasa mensual

                                    BigDecimal numerator = amount.multiply(interestRate);
                                    BigDecimal denominator = BigDecimal.ONE.subtract(
                                            BigDecimal.ONE.divide(
                                                    BigDecimal.valueOf(Math.pow(1 + interestRate.doubleValue(), months)),
                                                    10, RoundingMode.HALF_UP
                                            )
                                    );

                                    return numerator.divide(denominator, 2, RoundingMode.HALF_UP); // Renta mensual (R)
                                })
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Suma total mensual
    }

}