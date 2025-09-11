package co.com.pragma.usecase.user;

import co.com.pragma.model.user.model.LogIn;
import co.com.pragma.model.user.model.Token;
import co.com.pragma.model.user.gateways.UserRepository;
import reactor.core.publisher.Mono;

public class LogInUseCase {
    private final UserRepository userRepository;

    public LogInUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<Token> login(LogIn dto) {
        return userRepository.login(dto);
    }
}
