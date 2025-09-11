package co.com.pragma.model.loanapplication;

import co.com.pragma.model.pagination.LoanJoinPagination;
import co.com.pragma.model.pagination.LoanPaginationFinal;
import co.com.pragma.model.pagination.PaginationRequest;
import co.com.pragma.model.pagination.PaginationResponse;
import reactor.core.publisher.Mono;
import java.util.List;

public interface LoanApplicationRepository {
    Mono<LoanApplication> save(LoanApplication loanApplication);
    Mono<List<LoanApplication>> findByIdentificationNumberAndStatus(Long identificationNumber, Integer statusId);
    Mono<PaginationResponse<LoanPaginationFinal>> getPendingLoanApplications(PaginationRequest paginationRequest, Integer statusId);

}
