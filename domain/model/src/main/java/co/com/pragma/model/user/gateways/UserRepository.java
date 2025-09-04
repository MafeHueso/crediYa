package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.dto.LogInDTO;
import co.com.pragma.model.user.dto.TokenDTO;
import reactor.core.publisher.Mono;

public interface UserRepository {
   // Mono<User> saveUser(User user);
    Mono<User> findByEmail(String email);

    Mono<User> signUp(User user);
    Mono<TokenDTO> login(LogInDTO dto);

}

