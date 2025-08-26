package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.mapper.UserEntityMapper;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserEntity,
        Long,
        UserReactiveRepository
        > implements UserRepository {

    private final UserReactiveRepository repository;
    private final UserEntityMapper mapper;
    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, UserEntityMapper mapper, ObjectMapper objectMapper) {
        super(repository, objectMapper, entity -> mapper.toModel(entity));
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<User> saveUser(User user) {
            UserEntity entity = mapper.toEntity(user);  // Aquí haces el mapeo
            return repository.save(entity)
                    .map(mapper::toModel);
    }

    @Override
    public Mono<User> findByEmail(String email) {
            return repository.findByEmail(email)
                    .map(mapper::toModel);
    }
}
