package co.com.pragma.usecase.user;

import co.com.pragma.model.user.model.User;
import co.com.pragma.model.user.exception.EmailAlreadyExistsException;
import co.com.pragma.model.user.exception.EmailNotFoundException;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User(
                null,
                "John",
                "Doe",
                "john.doe@example.com",
                "1032578",
                "313310311",
                "ADMIN",
                new BigDecimal(9800000),
                null,
                "password123",
                true
        );
    }



    @Test
    void shouldSignUpSuccessfully_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByEmail(validUser.email())).thenReturn(Mono.empty());
        when(userRepository.signUp(any(User.class))).thenReturn(Mono.just(validUser));

        // Act & Assert
        StepVerifier.create(userUseCase.signUp(validUser))
                .expectNext(validUser)
                .verifyComplete();

        // Verify
        verify(userRepository).findByEmail(validUser.email());
        verify(userRepository).signUp(any(User.class));
    }

    @Test
    void shouldThrowError_WhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.findByEmail(validUser.email())).thenReturn(Mono.just(validUser));

        // Act & Assert
        StepVerifier.create(userUseCase.signUp(validUser))
                .expectErrorMatches(ex -> ex instanceof EmailAlreadyExistsException &&
                        ex.getMessage().equals("Email already registered"))
                .verify();

        // Verify
        verify(userRepository).findByEmail(validUser.email());
        verify(userRepository, never()).signUp(any());
    }

    @Test
    void shouldThrowError_WhenRequiredFieldsAreMissing() {
        // Test user with null first name
        User invalidUserNullFirstName = new User(null, null, "Doe", "test@example.com", "123", "123", "ADMIN", new BigDecimal(1000), null, "pass", true);
        StepVerifier.create(userUseCase.signUp(invalidUserNullFirstName))
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().contains("Required fields cannot be null or empty"))
                .verify();

        // Test user with blank last name
        User invalidUserBlankLastName = new User(null, "John", " ", "test@example.com", "123", "123", "ADMIN", new BigDecimal(1000), null, "pass", true);
        StepVerifier.create(userUseCase.signUp(invalidUserBlankLastName))
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().contains("Required fields cannot be null or empty"))
                .verify();

        // Test user with null salary
        User invalidUserNullSalary = new User(null, "John", "Doe", "test@example.com", "123", "123", "ADMIN", null, null, "pass", true);
        StepVerifier.create(userUseCase.signUp(invalidUserNullSalary))
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().contains("Required fields cannot be null or empty"))
                .verify();

        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).signUp(any());
    }

    @Test
    void shouldThrowError_WhenSalaryIsOutOfRange() {
        // Test user with salary less than or equal to 0
        User userWithZeroSalary = new User(null, "John", "Doe", "zero@example.com", "123", "123", "ADMIN", BigDecimal.ZERO, null, "pass", true);
        StepVerifier.create(userUseCase.signUp(userWithZeroSalary))
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().contains("Base salary must be greater than 0 and less than or equal to 15,000,000."))
                .verify();

        // Test user with salary above max allowed
        User userWithHighSalary = new User(null, "John", "Doe", "high@example.com", "123", "123", "ADMIN", new BigDecimal(15000001), null, "pass", true);
        StepVerifier.create(userUseCase.signUp(userWithHighSalary))
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().contains("Base salary must be greater than 0 and less than or equal to 15,000,000."))
                .verify();

        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).signUp(any());
    }

    // --- Tests para el método findByEmail ---

    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepository.findByEmail(validUser.email())).thenReturn(Mono.just(validUser));

        // Act & Assert
        StepVerifier.create(userUseCase.findByEmail(validUser.email()))
                .expectNext(validUser)
                .verifyComplete();

        // Verify
        verify(userRepository).findByEmail(validUser.email());
    }

    @Test
    void findByEmail_ShouldThrowError_WhenUserDoesNotExist() {
        // Arrange
        String nonExistentEmail = "notfound@example.com";
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userUseCase.findByEmail(nonExistentEmail))
                .expectErrorMatches(ex -> ex instanceof EmailNotFoundException &&
                        ex.getMessage().equals("Unregistered email"))
                .verify();

        // Verify
        verify(userRepository).findByEmail(nonExistentEmail);
    }

    // --- Tests para el método findByIdentificationNumber ---

    @Test
    void findByIdentificationNumber_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepository.findByIdentificationNumber(validUser.identificationNumber()))
                .thenReturn(Mono.just(validUser));

        // Act & Assert
        StepVerifier.create(userUseCase.findByIdentificationNumber(validUser.identificationNumber()))
                .expectNext(validUser)
                .verifyComplete();

        // Verify
        verify(userRepository).findByIdentificationNumber(validUser.identificationNumber());
    }

    @Test
    void findByIdentificationNumber_ShouldThrowError_WhenUserDoesNotExist() {
        // Arrange
        String nonExistentId = "99999999";
        when(userRepository.findByIdentificationNumber(nonExistentId))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userUseCase.findByIdentificationNumber(nonExistentId))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Unregistered identification number"))
                .verify();

        // Verify
        verify(userRepository).findByIdentificationNumber(nonExistentId);
    }
}