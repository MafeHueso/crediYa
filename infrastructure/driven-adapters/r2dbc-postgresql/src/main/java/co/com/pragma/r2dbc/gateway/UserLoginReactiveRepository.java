/*package co.com.pragma.r2dbc.gateway;

import co.com.pragma.r2dbc.document.UserLogin;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserLoginReactiveRepository extends ReactiveCrudRepository<UserLogin, String>, ReactiveQueryByExampleExecutor<UserLogin> {
    Mono<UserLogin> findByEmail(String email);
}
*/