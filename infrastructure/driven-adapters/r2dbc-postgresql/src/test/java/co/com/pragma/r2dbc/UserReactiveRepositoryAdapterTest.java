package co.com.pragma.r2dbc;

import static org.mockito.Mockito.*;

import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.adapter.UserReactiveRepositoryAdapter;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.gateway.UserReactiveRepository;
import co.com.pragma.r2dbc.mapper.UserEntityMapper;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    @InjectMocks
    private UserReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    private UserReactiveRepository repository;

    @Mock
    private UserEntityMapper mapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Logger logger;

    @Test
    void mustSaveUserSuccessfully() {
        // Setup
        User user = new User(1L, "Martina", "Sanchez", "marta123@gmail.com", null, null, 1, new BigDecimal("50000.00"), LocalDate.of(2000, 8, 1));
        UserEntity userEntity = new UserEntity(1L, "Martina", "Sanchez", "marta123@gmail.com", null, null, 1, new BigDecimal("50000.00"), LocalDate.of(2000, 8, 1));

        when(mapper.toEntity(user)).thenReturn(userEntity);
        when(repository.save(userEntity)).thenReturn(Mono.just(userEntity));
        when(mapper.toModel(userEntity)).thenReturn(user);
       //when(transactionalOperator.execute(any())).thenReturn(Mono.empty());

        // Call the method
        Mono<User> result = repositoryAdapter.saveUser(user);

        // Verify result to expect success
        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();

        // Verify interactions
        verify(mapper, times(1)).toEntity(user);
        verify(repository, times(1)).save(userEntity);
        verify(mapper, times(1)).toModel(userEntity);
        verify(transactionalOperator, times(1)).execute(any());
        verify(logger, times(1)).info("[saveUser] Saving user: {}", user);
        verify(logger, times(1)).info("[saveUser] User saved: {}", userEntity);
    }

    @Test
    void mustHandleErrorWhenSavingUser() {
        // Setup
        User user = new User(1L, "Martina", "Sanchez", "marta123@gmail.com", null, null, 1, new BigDecimal("50000.00"), LocalDate.of(2000, 8, 1));
        UserEntity userEntity = new UserEntity(1L, "Martina", "Sanchez", "marta123@gmail.com", null, null, 1, new BigDecimal("50000.00"), LocalDate.of(2000, 8, 1));

        when(mapper.toEntity(user)).thenReturn(userEntity);
        when(repository.save(userEntity)).thenReturn(Mono.just(userEntity));
        when(mapper.toModel(userEntity)).thenReturn(user);
        when(transactionalOperator.execute(any())).thenReturn(Flux.error(new RuntimeException("Transaction Error")));

        // Call the method
        Mono<User> result = repositoryAdapter.saveUser(user);

        // Verify result to expect error
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        // Verify interactions
        verify(mapper, times(1)).toEntity(user);
        verify(repository, times(1)).save(userEntity);
        verify(mapper, times(1)).toModel(userEntity);
        verify(transactionalOperator, times(1)).execute(any());
        verify(logger, times(1)).info("[saveUser] Saving user: {}", user);
        verify(logger, times(1)).error("[saveUser] Error saving user", Optional.ofNullable(any()));
    }

    @Test
    void mustFindUserByEmailSuccessfully() {
        // Setup
        String email = "john.doe@example.com";
        UserEntity userEntity = new UserEntity(1L, "Martina", "Sanchez", "marta123@gmail.com", null, null, 1, new BigDecimal("50000.00"), LocalDate.of(2000, 8, 1));
        User user = new User(1L, "Martina", "Sanchez", "marta123@gmail.com", null, null, 1, new BigDecimal("50000.00"), LocalDate.of(2000, 8, 1));

        when(repository.findByEmail(email)).thenReturn(Mono.just(userEntity));
        when(mapper.toModel(userEntity)).thenReturn(user);

        // Call the method
        Mono<User> result = repositoryAdapter.findByEmail(email);

        // Verify result
        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();

        // Verify interactions
        verify(repository, times(1)).findByEmail(email);
        verify(mapper, times(1)).toModel(userEntity);
        verify(logger, times(1)).info("[findByEmail] Looking for user with email: {}", email);
        verify(logger, times(1)).info("[findByEmail] User found: {}", userEntity);
    }

    @Test
    void mustHandleErrorWhenFindingUserByEmail() {
        // Setup
        String email = "marta123@gmail.com";
        when(repository.findByEmail(email)).thenReturn(Mono.error(new RuntimeException("Database Error")));

        // Call the method
        Mono<User> result = repositoryAdapter.findByEmail(email);

        // Verify that an error occurs
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        // Verify interactions
        verify(repository, times(1)).findByEmail(email);
        verify(logger, times(1)).info("[findByEmail] Looking for user with email: {}", email);
        verify(logger, times(1)).error("[findByEmail] Error finding user by email", Optional.ofNullable(any()));
    }
}
