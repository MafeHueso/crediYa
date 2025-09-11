package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.model.User;
import co.com.pragma.model.user.model.LogIn;
import co.com.pragma.model.user.model.Token;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> findByEmail(String email);

    Mono<User> signUp(User user);
    Mono<Token> login(LogIn dto);

    Mono<User>findByIdentificationNumber(Long identificationNumber);

}

