package co.com.pragma.api;

import co.com.pragma.api.request.LoanApplicationRequestDTO;
import co.com.pragma.model.exception.ExistApplicationTypeLoan;
import co.com.pragma.model.exception.InvalidLoanTypeException;
import co.com.pragma.model.exception.UserNotFoundException;
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

        return serverRequest.bodyToMono(LoanApplicationRequestDTO.class)
                .flatMap(req -> loanUseCase.createLoanApplication(
                                req.getEmail(),
                                req.getAmount(),
                                req.getTermMonths(),
                                req.getLoanTypeId()
                        )
                )
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Collections.singletonMap("Pending review", response))
                )
                .onErrorResume(UserNotFoundException.class, error -> ServerResponse.status(HttpStatus.NOT_FOUND)
                        .bodyValue(Collections.singletonMap("error", error.getMessage())))
                .onErrorResume(InvalidLoanTypeException.class, error -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .bodyValue(Collections.singletonMap("error", error.getMessage())))
                .onErrorResume(ExistApplicationTypeLoan.class, error -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .bodyValue(Collections.singletonMap("error", error.getMessage())));
    }

   }


