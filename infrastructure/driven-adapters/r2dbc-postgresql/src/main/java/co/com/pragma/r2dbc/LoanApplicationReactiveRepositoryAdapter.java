package co.com.pragma.r2dbc;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.r2dbc.entity.LoanApplicationEntity;
import co.com.pragma.r2dbc.gateways.LoanApplicationReactiveRepository;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.mapper.LoanApplicationEntityMapper;

import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Repository
public class LoanApplicationReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanApplication,            // Modelo de dominio
        LoanApplicationEntity,      // Entidad de datos
        Long,                       // Tipo del ID
        LoanApplicationReactiveRepository  // Repositorio
        > implements LoanApplicationRepository {

    private final LoanApplicationReactiveRepository repository;
    private final LoanApplicationEntityMapper mapper;
    private final TransactionalOperator transactionalOperator;

    public LoanApplicationReactiveRepositoryAdapter(LoanApplicationReactiveRepository repository,
                                                    LoanApplicationEntityMapper mapper,
                                                    ObjectMapper objectMapper,TransactionalOperator transactionalOperator) {
        super(repository, objectMapper, entity -> mapper.toModelAp(entity));
        this.repository = repository;
        this.mapper = mapper;
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<LoanApplication> saveLoan(LoanApplication loanApplication) {
        LoanApplicationEntity entityApp = mapper.toEntityAp(loanApplication);


        return transactionalOperator
                .execute(status -> repository.save(entityApp)
                        .map(mapper::toModelAp)
                )
                .doOnError(e -> log.error("Error saving loan application: {}", e.getMessage())
                ).single();
    }

}