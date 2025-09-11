package co.com.pragma.usecase.user;


import co.com.pragma.model.user.model.User;
import co.com.pragma.model.user.exception.EmailAlreadyExistsException;
import co.com.pragma.model.user.exception.EmailNotFoundException;
import co.com.pragma.model.user.gateways.UserRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public class UserUseCase {
    private final UserRepository userRepository;

    public UserUseCase(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    public Mono<User> signUp(User user) {
        if(user.firstName() == null || user.firstName().isBlank()
                || user.lastName() == null || user.lastName().isBlank()
                ||user.email() == null || user.email().isBlank()
                || user.salaryBase() == null){
            return Mono.error(new IllegalArgumentException("Required fields cannot be null or empty"));
        }
        if(user.salaryBase().compareTo(BigDecimal.ZERO) <= 0
                || user.salaryBase().compareTo(BigDecimal.valueOf(15000000)) > 0){
            return Mono.error(new IllegalArgumentException("Base salary must be greater than 0 and less than or equal to 15,000,000."));
        }

        return userRepository.findByEmail(user.email())
                .flatMap(existingUser -> Mono.<User>error(new EmailAlreadyExistsException("Email already registered")))
                .switchIfEmpty(userRepository.signUp(user));
    }

    public Mono<User> findByEmail(String email) {

        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new EmailNotFoundException("Unregistered email")));
    }

    public Mono<User> findByIdentificationNumber(Long identificationNumber) {

        return userRepository.findByIdentificationNumber(identificationNumber)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Unregistered identification number")));
    }

}





