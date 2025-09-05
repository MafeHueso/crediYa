import co.com.pragma.model.client.UserClientRepository;
import co.com.pragma.model.exception.*;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.LoanApplicationRepository;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.LoanTypeRepository;
import co.com.pragma.usecase.loanapplication.LoanApplicationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoanApplicationUseCaseTest {

    private LoanApplicationRepository loanApplicationRepository;
    private UserClientRepository clientRepository;
    private LoanTypeRepository loanTypeRepository;

    private LoanApplicationUseCase useCase;

    @BeforeEach
    void setUp() {
        loanApplicationRepository = mock(LoanApplicationRepository.class);
        clientRepository = mock(UserClientRepository.class);
        loanTypeRepository = mock(LoanTypeRepository.class);

        useCase = new LoanApplicationUseCase(loanApplicationRepository, clientRepository, loanTypeRepository);
    }

    @Test
    void shouldErrorWhenTermMonthsIsOutOfRange() {
        StepVerifier.create(useCase.createLoanApplication("email@test.com", BigDecimal.valueOf(1000), 5, 1L))
                .expectErrorMatches(e -> e instanceof TermLoanException &&
                        e.getMessage().equals("Loan term must be between 6 and 60 months"))
                .verify();
    }

    @Test
    void shouldErrorWhenUserNotFound() {
        when(clientRepository.getIdentificationByEmail(anyString())).thenReturn(Mono.justOrEmpty(null));

        StepVerifier.create(useCase.createLoanApplication("email@test.com", BigDecimal.valueOf(1000), 12, 1L))
                .expectErrorMatches(e -> e instanceof UserNotFoundException &&
                        e.getMessage().contains("User not found"))
                .verify();
    }

    @Test
    void shouldErrorWhenLoanTypeIsInvalid() {
        when(clientRepository.getIdentificationByEmail(anyString())).thenReturn(Mono.just("ID123"));
        when(loanTypeRepository.findByLoanTypeId(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.createLoanApplication("email@test.com", BigDecimal.valueOf(1000), 12, 1L))
                .expectErrorMatches(e -> e instanceof InvalidLoanTypeException &&
                        e.getMessage().equals("Invalid loan type."))
                .verify();
    }

    @Test
    void shouldErrorWhenAmountIsOutOfRange() {
        LoanType loanType = new LoanType(1L, "Personal Loan", BigDecimal.valueOf(5000), BigDecimal.valueOf(20000), BigDecimal.valueOf(0.1), true);

        when(clientRepository.getIdentificationByEmail(anyString())).thenReturn(Mono.just("ID123"));
        when(loanTypeRepository.findByLoanTypeId(anyLong())).thenReturn(Mono.just(loanType));

        StepVerifier.create(useCase.createLoanApplication("email@test.com", BigDecimal.valueOf(4000), 12, 1L))
                .expectErrorMatches(e -> e instanceof AmountTypeLoanException &&
                        e.getMessage().contains("Loan amount must be between"))
                .verify();
    }

    @Test
    void shouldErrorWhenPendingLoanExists() {
        LoanType loanType = new LoanType(1L, "Personal Loan", BigDecimal.valueOf(500000), BigDecimal.valueOf(2000), BigDecimal.valueOf(0.1), true);
        LoanApplication existingLoan = LoanApplication.builder()
                .loanTypeId(1L)
                .build();

        when(clientRepository.getIdentificationByEmail(anyString())).thenReturn(Mono.just("ID123"));
        when(loanTypeRepository.findByLoanTypeId(anyLong())).thenReturn(Mono.just(loanType));
        when(loanApplicationRepository.findByIdentificationNumberAndStatus(anyString(), eq(1)))
                .thenReturn(Mono.just(List.of(existingLoan)));

        StepVerifier.create(useCase.createLoanApplication("email@test.com", BigDecimal.valueOf(5000), 12, 1L))
                .expectErrorMatches(e -> e instanceof ExistApplicationTypeLoan &&
                        e.getMessage().contains("There is already a pending application"))
                .verify();
    }

    @Test
    void shouldCreateLoanSuccessfully() {
        LoanType loanType = new LoanType(1L, "Personal Loan", BigDecimal.valueOf(1000000), BigDecimal.valueOf(5000), BigDecimal.valueOf(0.1), true);

        when(clientRepository.getIdentificationByEmail(anyString())).thenReturn(Mono.just("ID123"));
        when(loanTypeRepository.findByLoanTypeId(anyLong())).thenReturn(Mono.just(loanType));
        when(loanApplicationRepository.findByIdentificationNumberAndStatus(anyString(), eq(1)))
                .thenReturn(Mono.just(List.of())); // No loans pending

        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(Mono.just(LoanApplication.builder()
                .identificationNumber("ID123")
                .amount(BigDecimal.valueOf(5000))
                .termMonths(12)
                .statusId(1)
                .loanTypeId(1L)
                .applicationDate(OffsetDateTime.now())
                .build()));

        StepVerifier.create(useCase.createLoanApplication("email@test.com", BigDecimal.valueOf(5000), 12, 1L))
                .expectNext("Pending review")
                .verifyComplete();
    }
}