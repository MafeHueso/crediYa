package co.com.pragma.model.loanapplication;

import co.com.pragma.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LoanApplicationRepository {
    Mono<LoanApplication> save(LoanApplication loanApplication);

    Mono<List<LoanApplication>> findByIdentificationNumberAndStatus(String identificationNumber, Integer statusId);





}
