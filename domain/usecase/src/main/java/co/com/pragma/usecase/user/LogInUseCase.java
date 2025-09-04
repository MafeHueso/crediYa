package co.com.pragma.usecase.user;

import co.com.pragma.model.user.dto.LogInDTO;
import co.com.pragma.model.user.dto.TokenDTO;
import co.com.pragma.model.user.gateways.UserRepository;
import reactor.core.publisher.Mono;

public class LogInUseCase {
    private final UserRepository userRepository;

    public LogInUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<TokenDTO> login(LogInDTO dto) {
        return userRepository.login(dto);
    }
}
