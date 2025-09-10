package co.com.pragma.r2dbc.loanType;

import co.com.pragma.model.exception.InvalidLoanTypeException;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.LoanTypeRepository;
import co.com.pragma.r2dbc.entity.LoanTypeEntity;
import co.com.pragma.r2dbc.loanType.LoanTypeReactiveRepository;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.mapper.LoanTypeEntityMapper;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class LoanTypeReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanType/* change for domain model */,
        LoanTypeEntity/* change for adapter model */,
        Long,
        LoanTypeReactiveRepository
> implements LoanTypeRepository {
    private final LoanTypeReactiveRepository repository;
    private final LoanTypeEntityMapper mapper;
    public LoanTypeReactiveRepositoryAdapter(LoanTypeReactiveRepository repository, LoanTypeEntityMapper mapper, ObjectMapper objectMapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, objectMapper, entity -> mapper.toModel(entity));
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    public Mono<LoanType> findByLoanTypeId(Long loanTypeId) {
        return repository.findByLoanTypeId(loanTypeId)
                .switchIfEmpty(Mono.error(new InvalidLoanTypeException("Loan type not found.")))
                .map(mapper::toModel);
    }
}




