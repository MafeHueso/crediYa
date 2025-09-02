package co.com.pragma.model.client;

import reactor.core.publisher.Mono;


public interface UserClientRepository {
    Mono<String> getIdentificationByEmail(String email);


}
