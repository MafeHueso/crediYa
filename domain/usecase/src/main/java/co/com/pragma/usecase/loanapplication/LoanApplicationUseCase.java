package co.com.pragma.usecase.loanapplication;

import co.com.pragma.model.client.UserClientRepository;
import co.com.pragma.model.exception.ExistApplicationTypeLoan;
import co.com.pragma.model.exception.InvalidLoanTypeException;
import co.com.pragma.model.exception.UserNotFoundException;
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

        return clientRepository.getIdentificationByEmail(email)
                .flatMap(identification -> {
                    if (identification == null) { //Validar si usuario existe
                        return Mono.error(new UserNotFoundException("User not found with email: " + email));
                    }
                    return loanTypeRepository.findByLoanTypeId(loanTypeId)  // Verificar que el tipo de préstamo existe
                            .switchIfEmpty(Mono.error(new InvalidLoanTypeException("Invalid loan type.")))
                            .flatMap(loanType -> {
                                return loanApplicationRepository.findByIdentificationNumberAndStatus(identification, 1)//validar si ya tiene una solicitud pendiente
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
