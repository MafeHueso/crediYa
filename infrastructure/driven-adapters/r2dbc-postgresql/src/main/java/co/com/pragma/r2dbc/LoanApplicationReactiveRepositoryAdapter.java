package co.com.pragma.r2dbc;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.LoanApplicationRepository;
import co.com.pragma.r2dbc.entity.LoanApplicationEntity;
import co.com.pragma.r2dbc.gateways.LoanApplicationReactiveRepository;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.mapper.LoanApplicationEntityMapper;

import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

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
        super(repository, objectMapper, entity -> mapper.toModel(entity));
        this.repository = repository;
        this.mapper = mapper;
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<LoanApplication> save(LoanApplication loanApplication) {
        LoanApplicationEntity entityApp = mapper.toEntity(loanApplication);

        return transactionalOperator
                .execute(status -> repository.save(entityApp)
                        .map(mapper::toModel)
                )
                .doOnError(e -> log.error("Error saving loan application: {}", e.getMessage())
                ).single();
    }

    @Override
    public Mono<List<LoanApplication>> findByIdentificationNumberAndStatus(String identificationNumber, Integer statusId) {
        return repository.findByIdentificationNumberAndStatusId(identificationNumber, statusId)
                .collectList()
                .map(loanEntities -> loanEntities.stream()
                        .map(mapper::toModel)
                        .collect(Collectors.toList())
                );
    }

}