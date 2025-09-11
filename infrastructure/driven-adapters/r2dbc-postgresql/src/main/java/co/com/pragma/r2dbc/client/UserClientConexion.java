package co.com.pragma.r2dbc.client;

import co.com.pragma.model.client.UserClientDetails;
import co.com.pragma.model.client.UserClientRepository;
import co.com.pragma.model.exception.UserNotFoundException;
import co.com.pragma.r2dbc.exception.InvalidTokenException;
import co.com.pragma.r2dbc.mapper.UserMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class UserClientConexion implements UserClientRepository {

    private final WebClient webClient;
    private final UserMapper userMapper;

    public UserClientConexion(WebClient.Builder webClient,
                              @Value("${config.base.endpoint.client}") String baseEndpoint,
                              UserMapper userMapper) {
        this.webClient = webClient
                .baseUrl(baseEndpoint)
                .build();
        this.userMapper = userMapper;
    }

    @Override
    public Mono<Long> getIdentificationByEmail(String email) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> {
                    Object credentials = ctx.getAuthentication().getCredentials();
                    if (credentials instanceof Jwt jwt) {
                        String token = jwt.getTokenValue();
                        log.info("Token JWT extraído: {}", token);
                        return token;
                    } else {
                        log.warn("Las credenciales no son del tipo Jwt: {}", credentials);
                        throw new IllegalStateException("Credenciales inválidas o no se encontró token JWT.");
                    }
                }) // Extrae token
                .flatMap(token -> webClient
                        .get()
                        .uri("/api/v1/login/email/{email}", email)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) // Inyecta token
                        .retrieve()
                        .onStatus(
                                status -> status.value() == 404,
                                response -> Mono.error(new UserNotFoundException("The user does not exist"))
                        )
                        .onStatus(
                                status -> status.is5xxServerError(),
                                response -> response.bodyToMono(String.class)
                                        .flatMap(body -> {
                                            if (body.toLowerCase().contains("bad token")) {
                                                return Mono.error(new InvalidTokenException("Invalid or expired token"));
                                            }
                                            return Mono.error(new RuntimeException("Internal server error in login service: " + body));
                                        })
                        )
                        .bodyToMono(ClientResponseDTO.class)
                        .map(ClientResponseDTO::getIdentificationNumber)
                );
    }

    public Mono<ClientResponseDTO> getUserInfo(String identificationNumber) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (Jwt) ctx.getAuthentication().getCredentials())
                .flatMap(jwt -> webClient.get()
                        .uri("/api/v1/login/id/{identificationNumber}", identificationNumber)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue())
                        .retrieve()
                        .bodyToMono(ClientResponseDTO.class)
                );
    }
    /*
    @Override
    public Mono<String> getIdentificationByEmail(String email) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> {
                    Object credentials = ctx.getAuthentication().getCredentials();
                    if (credentials instanceof Jwt jwt) {
                        return jwt.getTokenValue();
                    }
                    throw new IllegalStateException("Credenciales inválidas o no se encontró token JWT.");
                })
                .flatMap(token -> webClient
                        .get()
                        .uri("/api/v1/login/{email}", email)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .onStatus(status -> status.value() == 404,
                                response -> Mono.error(new UserNotFoundException("The user does not exist")))
                        .onStatus(status -> status.is5xxServerError(),
                                response -> response.bodyToMono(String.class).flatMap(body -> {
                                    if (body.toLowerCase().contains("bad token")) {
                                        return Mono.error(new InvalidTokenException("Invalid or expired token"));
                                    }
                                    return Mono.error(new RuntimeException("Internal server error: " + body));
                                }))
                        .bodyToMono(ClientResponseDTO.class)
                        .map(userMapper::toDomain)
                );
    }*/
}
