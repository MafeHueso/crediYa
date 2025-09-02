package co.com.pragma.api;

import co.com.pragma.api.dto.SaveUserDTO;
import co.com.pragma.api.mapper.UserDTOMapper;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exception.EmailAlreadyExistsException;
import co.com.pragma.model.user.exception.EmailNotFoundException;
import co.com.pragma.usecase.user.UserUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
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
public class Handler {

    private final UserUseCase userUseCase;
    private final UserDTOMapper userDTOMapper;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        log.info("[Handler] Received request to save user");
        return serverRequest.bodyToMono(SaveUserDTO.class)
                .doOnNext(dto -> log.debug("[Handler] Request body: {}", dto))
                .flatMap(dto -> {
                    var violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        log.warn("[Handler] Validation failed: {}", violations);
                        return Mono.error(new ConstraintViolationException(violations));
                    }

                    User user = userDTOMapper.toModel(dto);
                    return userUseCase.saveUser(user)
                            .doOnSuccess(savedUser -> log.info("[Handler] User saved successfully: {}", savedUser))
                            .map(userSaved -> userDTOMapper.toResponse(userSaved));
                })
                .flatMap(userDTO -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userDTO))
                .onErrorResume(EmailAlreadyExistsException.class, ex -> {
                    return ServerResponse.status(HttpStatus.CONFLICT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("message", ex.getMessage()
                            ));
                })
                .onErrorResume(ConstraintViolationException.class, ex -> {
                    var errors = ex.getConstraintViolations()
                            .stream()
                            .map(ConstraintViolation::getMessage)
                            .toList();
                    log.warn("[Handler] Responding with validation errors: {}", errors);
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("errors", errors));
                })
                .doOnError(e -> log.error("[Handler] Unexpected error", e));
    }

    public Mono<ServerResponse> listenFindByEmail(ServerRequest serverRequest) {
        String email = serverRequest.pathVariable("email");
        log.info("[Handler] Received request to find user by email: {}", email);
        return userUseCase.findByEmail(email)
                .map(userDTOMapper::toResponse)
                .flatMap(userDTO -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userDTO))
                .onErrorResume(EmailNotFoundException.class, error -> ServerResponse.status(HttpStatus.NOT_FOUND)
                .bodyValue(Collections.singletonMap("error", error.getMessage())))
                .doOnError(e -> log.error("[Handler] Error finding user by email", e));
    }


}
