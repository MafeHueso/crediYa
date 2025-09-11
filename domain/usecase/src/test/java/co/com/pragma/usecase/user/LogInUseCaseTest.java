package co.com.pragma.usecase.user;

import co.com.pragma.model.user.model.LogIn;
import co.com.pragma.model.user.model.Token;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class LogInUseCaseTest {


    private UserRepository userRepository;
    private LogInUseCase logInUseCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        logInUseCase = new LogInUseCase(userRepository);
    }

    @Test
    void shouldReturnTokenWhenLoginIsSuccessful() {
        LogIn dto = new LogIn("user@example.com", "password123");
        Token expectedToken = new Token("token_value");

        when(userRepository.login(dto)).thenReturn(Mono.just(expectedToken));

        StepVerifier.create(logInUseCase.login(dto))
                .expectNext(expectedToken)
                .verifyComplete();

        verify(userRepository).login(dto);
    }

    @Test
    void shouldPropagateErrorWhenLoginFails() {
        LogIn dto = new LogIn("user@example.com", "wrong_password");
        RuntimeException error = new RuntimeException("Bad credentials");

        when(userRepository.login(dto)).thenReturn(Mono.error(error));

        StepVerifier.create(logInUseCase.login(dto))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Bad credentials"))
                .verify();

        verify(userRepository).login(dto);
    }
}