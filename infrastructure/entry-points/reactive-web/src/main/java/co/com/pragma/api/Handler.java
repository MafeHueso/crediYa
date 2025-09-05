package co.com.pragma.api;

import co.com.pragma.api.exception.InvalidLoanRequestException;
import co.com.pragma.api.request.LoanApplicationRequestDTO;
import co.com.pragma.model.exception.*;
import co.com.pragma.usecase.loanapplication.LoanApplicationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class Handler {

    private final LoanApplicationUseCase loanUseCase;



    public Mono<ServerResponse> listenSaveLoan(ServerRequest serverRequest) {
        String email = serverRequest.exchange().getAttribute("email");
        String roleId = serverRequest.exchange().getAttribute("roleId");

        if (email == null || roleId == null) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .bodyValue(Collections.singletonMap("error", "Unauthorized: Missing authentication details"));
        }

        // Validar que el rol sea CLIENT para crear préstamo
        if (!"CLIENT".equals(roleId)) {
            return ServerResponse.status(HttpStatus.FORBIDDEN)
                    .bodyValue(Collections.singletonMap("error", "Only clients can create loan applications"));
        }

        // Aquí recibimos el body que viene con la solicitud
        return serverRequest.bodyToMono(LoanApplicationRequestDTO.class)
                .flatMap(loanApplicationRequestDTO -> {
                    // Validamos que el email del token coincida con el del request
                    if (!email.equalsIgnoreCase(loanApplicationRequestDTO.getEmail())) {
                        return Mono.error(new InvalidLoanRequestException("You cannot request a loan for another user."));
                    }
                    // Si está ok, seguimos con la lógica de negocio
                    return loanUseCase.createLoanApplication(
                            loanApplicationRequestDTO.getEmail(),
                            loanApplicationRequestDTO.getAmount(),
                            loanApplicationRequestDTO.getTermMonths(),
                            loanApplicationRequestDTO.getLoanTypeId()
                    );
                })
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Collections.singletonMap("Pending review", response))
                )
                // Manejo de errores
                .onErrorResume(UserNotFoundException.class, error -> ServerResponse.status(HttpStatus.NOT_FOUND)
                        .bodyValue(Collections.singletonMap("error", error.getMessage())))
                .onErrorResume(InvalidLoanTypeException.class, error -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .bodyValue(Collections.singletonMap("error", error.getMessage())))
                .onErrorResume(ExistApplicationTypeLoan.class, error -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .bodyValue(Collections.singletonMap("error", error.getMessage())))
                .onErrorResume(InvalidLoanRequestException.class, error -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .bodyValue(Collections.singletonMap("error", error.getMessage())))
                .onErrorResume(TermLoanException.class, error -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .bodyValue(Collections.singletonMap("error", error.getMessage())))
                .onErrorResume(AmountTypeLoanException.class, error -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .bodyValue(Collections.singletonMap("error", error.getMessage())));


    }

   }


