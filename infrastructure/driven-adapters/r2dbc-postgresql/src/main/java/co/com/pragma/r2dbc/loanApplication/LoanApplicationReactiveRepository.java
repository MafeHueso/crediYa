package co.com.pragma.r2dbc.loanApplication;

import co.com.pragma.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanApplicationReactiveRepository extends ReactiveCrudRepository<LoanApplicationEntity, Long>, ReactiveQueryByExampleExecutor<LoanApplicationEntity>{
    Flux<LoanApplicationEntity> findByIdentificationNumberAndStatusId(String identificationNumber, Integer statusId);
    Mono<Long> countByStatusId(Integer statusId);
    Flux<LoanApplicationEntity> findByStatusId(Integer statusId);


}
