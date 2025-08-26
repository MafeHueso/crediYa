package co.com.pragma.usecase.user;


import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public class UserUseCase {
    private final UserRepository userRepository;

    public UserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<User> saveUser(User user) {
        if(user.firstName() == null || user.firstName().isBlank()
                || user.lastName() == null || user.lastName().isBlank()
                ||user.email() == null || user.email().isBlank()
                || user.salaryBase() == null){
            return Mono.error(new IllegalArgumentException("Campos requeridos no pueden ser nulos o vacíos"));
        }
        if(user.salaryBase().compareTo(BigDecimal.ZERO) <= 0
                || user.salaryBase().compareTo(BigDecimal.valueOf(15000000)) > 0){
            return Mono.error(new IllegalArgumentException("Salario base debe ser mayor a 0 y menor o igual a 15.000.000"));
        }
        return userRepository.saveUser(user);

    }

    public Mono<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Mono.error(new IllegalArgumentException("El correo electrónico no puede ser nulo o vacío"));
        }
        return userRepository.findByEmail(email);
    }

}

