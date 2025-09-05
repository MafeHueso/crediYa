package co.com.pragma.usecase.loanapplication;

import co.com.pragma.model.client.UserClientRepository;
import co.com.pragma.model.exception.*;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.LoanApplicationRepository;
import co.com.pragma.model.loantype.LoanTypeRepository;
import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@RequiredArgsConstructor
public class LoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final UserClientRepository clientRepository;
    private final LoanTypeRepository loanTypeRepository;

    public Mono<String> createLoanApplication(String email, BigDecimal amount, Integer termMonths, Long loanTypeId) {

        if (termMonths < 6 || termMonths > 60) {
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
}