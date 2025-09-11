package co.com.pragma.model.client;

import reactor.core.publisher.Mono;


public interface UserClientRepository {
    Mono<Long> getIdentificationByEmail(String email);

  //  Mono<UserClientDetails>  getUserByIdentification(String identificationNumber);


}
