package co.com.pragma.r2dbc.loanApplication;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.LoanApplicationRepository;

import co.com.pragma.model.pagination.PaginationRequest;
import co.com.pragma.model.pagination.PaginationResponse;
import co.com.pragma.r2dbc.entity.LoanApplicationEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.mapper.LoanApplicationEntityMapper;

import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
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
                                                    ObjectMapper objectMapper,
                                                    TransactionalOperator transactionalOperator) {
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

    @Override
    public Mono<PaginationResponse<LoanApplication>> getPendingLoanApplications(PaginationRequest paginationRequest, Integer statusId) {
        int page = paginationRequest.page();
        int size = paginationRequest.size();

        int skip = page * size;

       Flux<LoanApplicationEntity> loanApplicationsFlux = repository.findByStatusId(statusId);

       Flux<LoanApplicationEntity> paginatedFlux = loanApplicationsFlux
                .skip(skip)
                .take(size);

        Mono<Long> totalElementsMono = repository.countByStatusId(statusId);

        return paginatedFlux
                .collectList()
                .zipWith(totalElementsMono)
                .map(tuple -> {
                    List<LoanApplication> content = tuple.getT1().stream()
                            .map(mapper::toModel)
                            .collect(Collectors.toList());

                    long totalElements = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) totalElements / paginationRequest.size());  // Calculamos el total de páginas

                    return new PaginationResponse<>(
                            content,
                            paginationRequest.page(),
                            paginationRequest.size(),
                            totalElements,
                            totalPages
                    );
                });
    }
/*
    @Override
    public Mono<PaginationResponse<ClientResponseDTO>> getPendingLoanApplications(PaginationRequest paginationRequest, Integer statusId) {
        int page = paginationRequest.page();
        int size = paginationRequest.size();
        int skip = page * size;

        Mono<Long> totalElementsMono = repository.countByStatusId(statusId);

        Flux<LoanApplicationEntity> loanApplicationsFlux = repository.findByStatusId(statusId)
                .skip(skip)
                .take(size);

        return loanApplicationsFlux
                .flatMap(loan ->
                        userClientRepository.getUserByIdentification(loan.getIdentificationNumber())
                                .zipWith(loanTypeRepository.findByLoanTypeId(loan.getLoanTypeId()))
                                .flatMap(tuple -> {
                                    UserClientDetails user = tuple.getT1();
                                    LoanType loanType = tuple.getT2();

                                    return calculateTotalMonthlyDebt(user.getEmail())
                                            .map(deudaTotal -> {
                                                // Armamos el DTO final
                                                return ClientResponseDTO.builder()
                                                        .amount(loan.getAmount())
                                                        .termMonths(loan.getTermMonths())
                                                        .email(user.getEmail())
                                                        .name(user.getName())
                                                        .loanTypeId(String.valueOf(loan.getLoanTypeId()))
                                                        .interestRate(loanType.getInterestRate())
                                                        .statusId(String.valueOf(loan.getStatusId()))
                                                        .identificationNumber(loan.getIdentificationNumber())
                                                        .salaryBase(user.getBaseSalary())
                                                        .totalMonthlyDebtApprovals(deudaTotal)
                                                        .build();
                                            });
                                })
                )
                .collectList()
                .zipWith(totalElementsMono)
                .map(tuple -> {
                    List<ClientResponseDTO> content = tuple.getT1();
                    long totalElements = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) totalElements / size);

                    return new PaginationResponse<>(
                            content,
                            page,
                            size,
                            totalElements,
                            totalPages
                    );
                });
    }

*/

}