package co.com.pragma.api.error;

import co.com.pragma.model.user.exception.EmailAlreadyExistsException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Order(-2)
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Map<String, Object> response = new HashMap<>();

        if (ex instanceof ConstraintViolationException e) {
            status = HttpStatus.BAD_REQUEST;
            response.put("error", "Validation Failed");
            response.put("message", "Input data is invalid");

            List<String> errors = e.getConstraintViolations()
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();

            response.put("details", errors);

        } else if (ex instanceof EmailAlreadyExistsException e) {
            status = HttpStatus.CONFLICT;
            response.put("error", "Email Conflict");
            response.put("message", e.getMessage());

        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.put("error", "Internal Server Error");
            response.put("message", ex.getMessage());
        }

        log.error("[GlobalExceptionHandler] {}: {}", status, ex.getMessage(), ex);

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String json = new JSONObject(response).toString();
        var buffer = exchange.getResponse().bufferFactory()
                .wrap(json.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
