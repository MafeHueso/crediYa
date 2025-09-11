package co.com.pragma.r2dbc.loanApplication;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.LoanApplicationRepository;

import co.com.pragma.model.pagination.LoanJoinPagination;
import co.com.pragma.model.pagination.LoanPaginationFinal;
import co.com.pragma.model.pagination.PaginationRequest;
import co.com.pragma.model.pagination.PaginationResponse;
import co.com.pragma.r2dbc.client.UserClientConexion;
import co.com.pragma.r2dbc.entity.LoanApplicationEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.mapper.LoanApplicationEntityMapper;

import co.com.pragma.r2dbc.mapper.PendingLoanMapper;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
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

    private final DatabaseClient databaseClient;
    private final UserClientConexion userClientConexion;
    private final PendingLoanMapper pendingLoanMapper;


    public LoanApplicationReactiveRepositoryAdapter(LoanApplicationReactiveRepository repository,
                                                    LoanApplicationEntityMapper mapper,
                                                    ObjectMapper objectMapper,
                                                    TransactionalOperator transactionalOperator,
                                                    DatabaseClient databaseClient,
                                                    UserClientConexion userClientConexion,
                                                    PendingLoanMapper pendingLoanMapper) {
        super(repository, objectMapper, entity -> mapper.toModel(entity));
        this.repository = repository;
        this.mapper = mapper;
        this.transactionalOperator = transactionalOperator;
        this.databaseClient = databaseClient;
        this.userClientConexion = userClientConexion;
        this.pendingLoanMapper = pendingLoanMapper;

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
    public Mono<List<LoanApplication>> findByIdentificationNumberAndStatus(Long identificationNumber, Integer statusId) {
        return repository.findByIdentificationNumberAndStatusId(identificationNumber, statusId)
                .collectList()
                .map(loanEntities -> loanEntities.stream()
                        .map(mapper::toModel)
                        .collect(Collectors.toList())
                );
    }
        @Override
        public Mono<PaginationResponse<LoanPaginationFinal>> getPendingLoanApplications(PaginationRequest paginationRequest, Integer statusId) {
            int page = paginationRequest.page();
            int size = paginationRequest.size();

            int offset = page * size;

            Mono<Long> totalElementsMono = countPendingLoanApplications(statusId);

            Flux<LoanJoinPagination> loanApplicationsFlux = findPendingLoanApplications(size, offset, statusId)
                    .doOnNext(loan -> log.info("Loan data desde BD: {}", loan));

            return loanApplicationsFlux
                    .flatMap(
                            loan -> {
                                {
                                    log.info("Consultando usuario con identificación: {}", loan.identificationNumber());

                                    return userClientConexion.getUserInfo(loan.identificationNumber())
                                            .doOnNext(userInfo -> log.info("Respuesta de auth: {}", userInfo))
                                            .map(userInfo -> new PendingLoanResponseDTO(
                                                    loan.amount(),
                                                    loan.termMonths(),
                                                    userInfo.getEmail(),
                                                    userInfo.getFirstName() + " " + userInfo.getLastName(),
                                                    loan.loanName(),
                                                    loan.interestRate(),
                                                    loan.status_name(),
                                                    userInfo.getSalaryBase()
                                            ))
                                            .onErrorResume(e -> {
                                                log.error("Error llamando a Auth para identificación {}: {}", loan.identificationNumber(), e.getMessage());
                                                return Mono.just(new PendingLoanResponseDTO(
                                                    loan.amount(),
                                                    loan.termMonths(),
                                                    null,
                                                    null,
                                                    loan.loanName(),
                                                    loan.interestRate(),
                                                    loan.status_name(),
                                                    null
                                            ));
                                            });
                                }
                            })
                    .collectList()
                    .zipWith(totalElementsMono)
                    .map(tuple -> {
                        List<PendingLoanResponseDTO> content = tuple.getT1();
                        long totalElements = tuple.getT2();
                        List<LoanPaginationFinal> finalDtos = pendingLoanMapper.toLoanPaginationFinalList(content);
                        int totalPages = (int) Math.ceil((double) totalElements / size);

                        return new PaginationResponse<>(
                                finalDtos,
                                page,
                                size,
                                totalElements,
                                totalPages
                        );
                    });
    }


    public Flux<LoanJoinPagination> findPendingLoanApplications(int limit, int offset, Integer statusId) {
        return databaseClient.sql(
                        "SELECT la.identification_number, la.amount, la.term_months, lt.loan_name, lt.interest_rate, s.status_name " +
                                "FROM loan_applications la " +
                                "JOIN loan_type lt ON la.loan_type_id = lt.loan_type_id " +
                                "JOIN status s ON la.status_id = s.status_id " +
                                "WHERE la.status_id = :statusId " +
                                "ORDER BY la.application_id " +
                                "LIMIT :limit OFFSET :offset"
                )
                .bind("statusId", statusId)
                .bind("limit", limit)
                .bind("offset", offset)
                .map(row -> new LoanJoinPagination(
                        row.get("identification_number", String.class),
                        row.get("amount", BigDecimal.class),
                        row.get("term_months", Integer.class),
                        row.get("loan_name", String.class),
                        row.get("interest_rate", BigDecimal.class),
                        row.get("status_name", String.class)
                ))
                .all();
    }

    public Mono<Long> countPendingLoanApplications(Integer statusId) {
        return databaseClient.sql(
                        "SELECT COUNT(*) FROM loan_applications WHERE status_id = :statusId"
                )
                .bind("statusId", statusId)
                .map(row -> row.get(0, Long.class))
                .one();
    }

}