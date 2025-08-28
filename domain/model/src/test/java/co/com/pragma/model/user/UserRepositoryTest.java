package co.com.pragma.model.user;

import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest {
    @Mock
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User(1L, "Erik", "Ussa", "eussa@gmail.com", null, null, 1, new BigDecimal("50000.00"), LocalDate.of(2000, 8, 1));
    }

    @Test
    void saveUser_Success() {
        // Configura el comportamiento del mock
        when(userRepository.saveUser(user)).thenReturn(Mono.just(user));

        // Llamada al método que se va a probar
        StepVerifier.create(userRepository.saveUser(user))
                .expectNext(user)
                .verifyComplete();

        // Verifica que se haya llamado el método de saveUser exactamente una vez
        verify(userRepository, times(1)).saveUser(user);
    }

    @Test
    void saveUser_Error() {
        // Simula un error durante el guardado
        when(userRepository.saveUser(user)).thenReturn(Mono.error(new RuntimeException("Error al guardar el usuario")));

        StepVerifier.create(userRepository.saveUser(user))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void findByEmail_UserExists() {
        // Configura el mock para devolver un usuario cuando busque por email
        when(userRepository.findByEmail(user.email())).thenReturn(Mono.just(user));

        StepVerifier.create(userRepository.findByEmail(user.email()))
                .expectNext(user)
                .verifyComplete();

        // Verifica que se haya llamado a findByEmail
        verify(userRepository, times(1)).findByEmail(user.email());
    }

    @Test
    void findByEmail_UserNotFound() {
        // Configura el mock para devolver Mono.empty() cuando el usuario no se encuentra
        when(userRepository.findByEmail(user.email())).thenReturn(Mono.empty());

        StepVerifier.create(userRepository.findByEmail(user.email()))
                .expectNextCount(0)  // No debería devolver ningún usuario
                .verifyComplete();

        // Verifica que se haya llamado a findByEmail
        verify(userRepository, times(1)).findByEmail(user.email());
    }

}

