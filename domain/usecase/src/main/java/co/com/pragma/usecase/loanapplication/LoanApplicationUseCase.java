package co.com.pragma.usecase.loanapplication;

import co.com.pragma.model.exception.BusinessException;
import co.com.pragma.model.exception.TypeLoanNotFoundException;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;

        public Mono<LoanApplication> saveLoan(LoanApplication loanApplication) {
        return loanTypeRepository.findByLoanTypeId(loanApplication.loanTypeId())
                .flatMap(loanType -> {
                    if (loanApplication.amount().compareTo(loanType.minAmount()) < 0 ||
                            loanApplication.amount().compareTo(loanType.maxAmount()) > 0) {
                        return Mono.error(new BusinessException("Amount outside the permitted range"));
                    }
                    return loanApplicationRepository.saveLoan(loanApplication);
                });
    }

    public Mono<LoanType> findByTypeLoan(Long loanTypeId) {

        return loanTypeRepository.findByLoanTypeId(loanTypeId);
    }



}
