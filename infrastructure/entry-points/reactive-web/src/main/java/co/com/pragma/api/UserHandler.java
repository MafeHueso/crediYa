package co.com.pragma.api;


import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.api.error.InvalidRolRequestException;
import co.com.pragma.api.mapper.UserDTOMapper;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.dto.LogInDTO;
import co.com.pragma.model.user.dto.TokenDTO;
import co.com.pragma.model.user.exception.BadCredentialsException;
import co.com.pragma.model.user.exception.EmailAlreadyExistsException;
import co.com.pragma.model.user.exception.EmailNotFoundException;
import co.com.pragma.usecase.user.LogInUseCase;
import co.com.pragma.usecase.user.UserUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserHandler {
    private final UserUseCase userUseCase;
    private final LogInUseCase logInUseCase;
    private final UserDTOMapper userDTOMapper;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public Mono<ServerResponse> signUp(ServerRequest request) {
        String roleId = request.exchange().getAttribute("roleId");

        if (roleId == null || !"ADMIN".equalsIgnoreCase(roleId)) {
            return ServerResponse.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Mono.error(new InvalidRolRequestException("Only ADMIN users can create new user accounts.")));
        }

        return request.bodyToMono(UserDTO.class)
                .flatMap(userDTO -> {
                    var violations = validator.validate(userDTO);
                    if (!violations.isEmpty()) {
                        return Mono.error(new ConstraintViolationException(violations));
                    }

                    User user = userDTOMapper.toModel(userDTO);
                    return userUseCase.signUp(user)
                            .map(userSaved -> userDTOMapper.toResponse(userSaved));
                })
                .flatMap(userSaved -> {
                    String message = "Successful registration for user " + userSaved.getEmail();
                    Map<String, String> response = Map.of("message", message);
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                })
                .onErrorResume(EmailAlreadyExistsException.class, ex ->
                        ServerResponse.status(HttpStatus.CONFLICT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("message", ex.getMessage()))
                )
                .onErrorResume(ConstraintViolationException.class, ex -> {
                    var errors = ex.getConstraintViolations()
                            .stream()
                            .map(ConstraintViolation::getMessage)
                            .toList();

                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("errors", errors));
                });
    }


    public Mono<ServerResponse> logIn(ServerRequest request) {

        return request.bodyToMono(LogInDTO.class)
                .flatMap(dto ->
                        logInUseCase.login(dto)
                                .flatMap(tokenDTO -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(tokenDTO))
                )
                .onErrorResume(BadCredentialsException.class, ex ->
                        ServerResponse.status(HttpStatus.UNAUTHORIZED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("message", ex.getMessage()))
                );
    }

    public Mono<ServerResponse> findByEmail(ServerRequest serverRequest) {
        String email = serverRequest.pathVariable("email");

        return userUseCase.findByEmail(email)
                .map(userDTOMapper::toDomain)
                .flatMap(dto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(dto))
                .onErrorResume(EmailNotFoundException.class, error -> ServerResponse.status(HttpStatus.NOT_FOUND)
                        .bodyValue(Collections.singletonMap("error", error.getMessage())))
                .doOnError(e -> log.error("[Handler] Error finding user by email", e));
    }


}
