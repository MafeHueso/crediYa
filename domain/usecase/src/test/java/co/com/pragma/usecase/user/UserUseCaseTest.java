package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exception.EmailAlreadyExistsException;
import co.com.pragma.model.user.exception.EmailNotFoundException;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private final User validUser = new User(
            null,
            "John",
            "Doe",
            "john.doe@example.com",
            1032578L,
            "313310311",
            "ADMIN",
            new BigDecimal(9800000),
            null,
            "password123",
            true
    );

    @Test
    void shouldSignUpSuccessfully() {
        // Mock: no existe usuario
        when(userRepository.findByEmail(validUser.email())).thenReturn(Mono.empty());
        when(userRepository.signUp(validUser)).thenReturn(Mono.just(validUser));

        StepVerifier.create(userUseCase.signUp(validUser))
                .expectNext(validUser)
                .verifyComplete();

        verify(userRepository).findByEmail(validUser.email());
        verify(userRepository).signUp(validUser);
    }

    @Test
    void shouldThrowErrorWhenFieldsAreMissing() {
        User invalidUser = new User(null, null, " ", null, null,
                "313310311", "ADMIN", null, null, "password123", true);

        StepVerifier.create(userUseCase.signUp(invalidUser))
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().contains("Required fields cannot be null"))
                .verify();
    }

    @Test
    void shouldThrowErrorWhenSalaryIsOutOfRange() {
        User tooHighSalaryUser = new User(
                null,
                "John",
                "Doe",
                "john.doe@example.com",
                1032578L,
                "313310311",
                "ADMIN",
                new BigDecimal(258000000),
                null,
                "password123",
                true  // fuera del rango permitido
        );

        StepVerifier.create(userUseCase.signUp(tooHighSalaryUser))
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().contains("Base salary must be greater than 0"))
                .verify();
    }

    @Test
    void shouldThrowErrorWhenEmailAlreadyExists() {
        User user = new User(
                null,
                "John",
                "Doe",
                "john.doe@example.com",
                1032578L,
                "313310311",
                "ADMIN",
                new BigDecimal(20000),
                null,
                "password123",
                true );

        // Simular que ya existe usuario con ese email
        when(userRepository.findByEmail(user.email())).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.signUp(user))
                .expectErrorMatches(ex -> ex instanceof EmailAlreadyExistsException &&
                        ex.getMessage().equals("Email already registered"))
                .verify();
    }

    @Test
    void shouldReturnUserWhenEmailExists() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Mono.just(validUser));

        StepVerifier.create(userUseCase.findByEmail("john.doe@example.com"))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    void shouldThrowErrorWhenEmailNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.findByEmail("notfound@example.com"))
                .expectErrorMatches(ex -> ex instanceof EmailNotFoundException &&
                        ex.getMessage().contains("Unregistered email"))
                .verify();
    }
}