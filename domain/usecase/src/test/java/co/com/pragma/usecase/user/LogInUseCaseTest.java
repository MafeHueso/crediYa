package co.com.pragma.usecase.user;

import co.com.pragma.model.user.dto.LogInDTO;
import co.com.pragma.model.user.dto.TokenDTO;
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
        LogInDTO dto = new LogInDTO("user@example.com", "password123");
        TokenDTO expectedToken = new TokenDTO("token_value");

        when(userRepository.login(dto)).thenReturn(Mono.just(expectedToken));

        StepVerifier.create(logInUseCase.login(dto))
                .expectNext(expectedToken)
                .verifyComplete();

        verify(userRepository).login(dto);
    }

    @Test
    void shouldPropagateErrorWhenLoginFails() {
        LogInDTO dto = new LogInDTO("user@example.com", "wrong_password");
        RuntimeException error = new RuntimeException("Bad credentials");

        when(userRepository.login(dto)).thenReturn(Mono.error(error));

        StepVerifier.create(logInUseCase.login(dto))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Bad credentials"))
                .verify();

        verify(userRepository).login(dto);
    }
}