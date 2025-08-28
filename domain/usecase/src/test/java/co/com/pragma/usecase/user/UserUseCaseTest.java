package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exception.EmailAlreadyExistsException;
import co.com.pragma.model.user.exception.EmailNotFoundException;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

public class UserUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1L, "Martina", "Sanchez", "marta123@gmail.com", null, null, 1, new BigDecimal("50000.00"), LocalDate.of(2000, 8, 1));
    }


    @Test
    void saveUser_EmailAlreadyExists() {
        // Simulamos que el correo ya existe
        when(userRepository.findByEmail(user.email())).thenReturn(Mono.just(user));

        // Llamamos al caso de uso
        StepVerifier.create(userUseCase.saveUser(user))
                .expectError(EmailAlreadyExistsException.class)
                .verify();

        verify(userRepository, times(1)).findByEmail(user.email());
        verify(userRepository, never()).saveUser(user);
    }

    @Test
    void saveUser_Success() {
        // Simulamos que el correo no existe
        when(userRepository.findByEmail(user.email())).thenReturn(Mono.empty());
        when(userRepository.saveUser(user)).thenReturn(Mono.just(user));

        // Llamamos al caso de uso
        StepVerifier.create(userUseCase.saveUser(user))
                .expectNext(user)  // Verifica que el usuario fue guardado correctamente
                .verifyComplete();

        verify(userRepository, times(1)).findByEmail(user.email());
        verify(userRepository, times(1)).saveUser(user);
    }

    @Test
    void findByEmail_UserNotFound() {
        // Simulamos que no existe el correo
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Mono.empty());

        // Llamamos al caso de uso
        StepVerifier.create(userUseCase.findByEmail(email))
                .expectError(EmailNotFoundException.class)
                .verify();

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void findByEmail_UserFound() {
        // Simulamos que el correo ya existe
        when(userRepository.findByEmail(user.email())).thenReturn(Mono.just(user));

        // Llamamos al caso de uso
        StepVerifier.create(userUseCase.findByEmail(user.email()))
                .expectNext(user)
                .verifyComplete();

        verify(userRepository, times(1)).findByEmail(user.email());
    }

}