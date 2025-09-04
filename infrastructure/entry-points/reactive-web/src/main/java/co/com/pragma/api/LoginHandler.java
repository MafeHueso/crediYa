package co.com.pragma.api;


import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.api.mapper.UserDTOMapper;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.dto.LogInDTO;
import co.com.pragma.model.user.dto.TokenDTO;
import co.com.pragma.model.user.exception.EmailAlreadyExistsException;
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

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginHandler {
    private final UserUseCase userUseCase;
    private final LogInUseCase logInUseCase;
    private final UserDTOMapper userDTOMapper;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public Mono<ServerResponse> signUp(ServerRequest request) {

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
                .flatMap(userDTO -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("Successful registration", userDTO))
                        .onErrorResume(EmailAlreadyExistsException.class, ex -> {
                            return ServerResponse.status(HttpStatus.CONFLICT)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(Map.of("message", ex.getMessage()));
                        })
                        .onErrorResume(ConstraintViolationException.class, ex -> {
                            var errors = ex.getConstraintViolations()
                                    .stream()
                                    .map(ConstraintViolation::getMessage)
                                    .toList();

                            return ServerResponse.badRequest()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(Map.of("errors", errors));
                        })
                );
    }


    public Mono<ServerResponse> logIn(ServerRequest request) {

        return request.bodyToMono(LogInDTO.class)
                .flatMap(dto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(logInUseCase.login(dto), TokenDTO.class));
    }

}
