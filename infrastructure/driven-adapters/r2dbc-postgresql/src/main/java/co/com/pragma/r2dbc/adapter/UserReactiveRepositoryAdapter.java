/*package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.gateway.UserReactiveRepository;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.mapper.UserEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserEntity,
        Long,
        UserReactiveRepository

        > implements UserRepository {

    private final UserReactiveRepository repository;
    private final UserEntityMapper mapper;
    private final TransactionalOperator transactionalOperator;
    private static final Logger log = LoggerFactory.getLogger(UserReactiveRepositoryAdapter.class);
    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, UserEntityMapper mapper,
                                         ObjectMapper objectMapper,  TransactionalOperator transactionalOperator) {
        super(repository, objectMapper, entity -> mapper.toModel(entity));
        this.repository = repository;
        this.mapper = mapper;
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<User> saveUser(User user) {
        log.info("[saveUser] Saving user: {}", user);
            UserEntity entity = mapper.toEntity(user);
             return transactionalOperator
                .execute(status -> repository.save(entity)
                        .doOnNext(saved -> log.info("[saveUser] User saved: {}", saved))
                        .map(mapper::toModel)
                )
                .doOnError(error -> log.error("[saveUser] Error saving user", error))
                .single();
    }

    @Override
    public Mono<User> findByEmail(String email) {
        log.info("[findByEmail] Looking for user with email: {}", email);
            return repository.findByEmail(email)
                    .doOnNext(userEntity -> log.info("[findByEmail] User found: {}", userEntity))
                    .doOnError(error -> log.error("[findByEmail] Error finding user by email", error))
                    .map(mapper::toModel);
    }

}
*/