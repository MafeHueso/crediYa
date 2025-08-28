package co.com.pragma.usecase.user;


import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exception.EmailAlreadyExistsException;
import co.com.pragma.model.user.exception.EmailNotFoundException;
import co.com.pragma.model.user.gateways.UserRepository;
import reactor.core.publisher.Mono;

public class UserUseCase {
    private final UserRepository userRepository;

    public UserUseCase(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    public Mono<User> saveUser(User user) {
        return userRepository.findByEmail(user.email())
                .flatMap(existingUser -> {
                    if (existingUser != null) {
                        return Mono.error(new EmailAlreadyExistsException("Email already registered"));
                    }
                    return userRepository.saveUser(user);
                });
    }

    public Mono<User> findByEmail(String email) {

        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new EmailNotFoundException("Unregistered email")));
    }
}





