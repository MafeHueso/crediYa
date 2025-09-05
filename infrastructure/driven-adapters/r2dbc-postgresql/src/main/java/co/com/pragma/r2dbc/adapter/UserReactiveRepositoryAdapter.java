package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.dto.LogInDTO;
import co.com.pragma.model.user.dto.TokenDTO;
import co.com.pragma.model.user.exception.BadCredentialsException;
import co.com.pragma.model.user.exception.EmailAlreadyExistsException;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.gateway.UserReactiveRepository;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.mapper.UserEntityMapper;
import co.com.pragma.r2dbc.security.jwt.JwtProvider;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;


@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserEntity,
        Long,
        UserReactiveRepository

     > implements UserRepository {
    private final UserReactiveRepository userReactiveRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserEntityMapper userEntityMapper;
    private final TransactionalOperator transactionalOperator;

    public UserReactiveRepositoryAdapter(UserReactiveRepository userReactiveRepository, UserEntityMapper userEntityMapper,
                                         ObjectMapper objectMapper, PasswordEncoder passwordEncoder, JwtProvider jwtProvider,
                                         TransactionalOperator transactionalOperator) {
        super(userReactiveRepository,  objectMapper, userEntityMapper::toModel);

        this.userReactiveRepository = userReactiveRepository;
        this.userEntityMapper = userEntityMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<User> signUp(User user) {
        UserEntity userEntity = userEntityMapper.toEntity(user);

        userEntity.setPassword(passwordEncoder.encode(user.password()));

        return userReactiveRepository.findByEmail(user.email())
                .hasElement()
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new EmailAlreadyExistsException("Email already registered"));
                    } else {
                        return userReactiveRepository.save(userEntity)
                                .map(userEntityMapper::toModel);
                    }
                })
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<User> findByEmail(String email) {

        return userReactiveRepository.findByEmail(email)
                .map(userEntityMapper::toModel);
    }
    @Override
    public Mono<TokenDTO> login(LogInDTO dto) {
        return userReactiveRepository.findByEmail(dto.email())
                .filter(userDocument -> passwordEncoder.matches(dto.password(), userDocument.getPassword()))
                .map(userDocument -> new TokenDTO(jwtProvider.generateToken(userDocument)))
                .switchIfEmpty(Mono.error(new BadCredentialsException("Bad credentials")));
    }
}
