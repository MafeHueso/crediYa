package co.com.pragma.r2dbc;

import co.com.pragma.model.client.UserClientDetails;
import co.com.pragma.model.client.UserClientRepository;
import co.com.pragma.model.exception.NotFoundException;
import co.com.pragma.model.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
public class UserClientConexion implements UserClientRepository {

    private final WebClient webClient;

    public UserClientConexion(WebClient.Builder webClient, @Value("${config.base.endpoint.client}") String baseEndpoint) {
        this.webClient = webClient
                .baseUrl(baseEndpoint)
                .build();
    }

    @Override
    public Mono<String> getIdentificationByEmail(String email) {
        return webClient
                .get()
                .uri("/api/v1/users/{email}", email)
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        response -> Mono.error(new UserNotFoundException("The user does not exist"))
                )
                .bodyToMono(ClientResponseDTO.class)
                .map(ClientResponseDTO::getIdentificationNumber);
    }
}
