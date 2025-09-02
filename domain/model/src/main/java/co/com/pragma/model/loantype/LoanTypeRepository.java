package co.com.pragma.model.loantype;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loantype.LoanType;
import reactor.core.publisher.Mono;

public interface LoanTypeRepository {
  Mono<LoanType> findByLoanTypeId(Long loanTypeId);

}
