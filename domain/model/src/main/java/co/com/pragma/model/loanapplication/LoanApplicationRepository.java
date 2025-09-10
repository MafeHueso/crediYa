package co.com.pragma.model.loanapplication;

import co.com.pragma.model.pagination.PaginationRequest;
import co.com.pragma.model.pagination.PaginationResponse;
import reactor.core.publisher.Mono;
import java.util.List;

public interface LoanApplicationRepository {
    Mono<LoanApplication> save(LoanApplication loanApplication);
    Mono<List<LoanApplication>> findByIdentificationNumberAndStatus(String identificationNumber, Integer statusId);
    Mono<PaginationResponse<LoanApplication>> getPendingLoanApplications(PaginationRequest paginationRequest, Integer statusId);

}
