package co.com.pragma.api;

import co.com.pragma.api.exception.InvalidLoanRequestException;
import co.com.pragma.api.saveApplication.LoanApplicationRequestDTO;
import co.com.pragma.model.exception.*;
import co.com.pragma.model.pagination.PaginationRequest;
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

    public Mono<ServerResponse> listenGetPendingLoanApplications(ServerRequest serverRequest) {

        String statusIdParam = serverRequest.queryParam("statusId").orElse("0");
        String pageParam = serverRequest.queryParam("page").orElse("0");
        String sizeParam = serverRequest.queryParam("size").orElse("10");

        int statusId = Integer.parseInt(statusIdParam);
        int page = Integer.parseInt(pageParam);
        int size = Integer.parseInt(sizeParam);

        PaginationRequest pageable = new PaginationRequest(page, size);

        return loanUseCase.getPendingLoanApplications(pageable, statusId)
                .flatMap(response ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response)
                )
                .onErrorResume(Exception.class, error -> {
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .bodyValue(Collections.singletonMap("error", "An error occurred: " + error.getMessage()));
                });
    }

    public Mono<ServerResponse> listenSaveLoan(ServerRequest serverRequest) {
        String email = serverRequest.exchange().getAttribute("email");
        String roleId = serverRequest.exchange().getAttribute("roleId");

        if (email == null || roleId == null) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .bodyValue(Collections.singletonMap("error", "Unauthorized: Missing authentication details"));
        }

        if (!"CLIENT".equals(roleId)) {
            return ServerResponse.status(HttpStatus.FORBIDDEN)
                    .bodyValue(Collections.singletonMap("error", "Only clients can create loan applications"));
        }

        return serverRequest.bodyToMono(LoanApplicationRequestDTO.class)
                .flatMap(loanApplicationRequestDTO -> {
                    if (!email.equalsIgnoreCase(loanApplicationRequestDTO.getEmail())) {
                        return Mono.error(new InvalidLoanRequestException("You cannot request a loan for another user."));
                    }
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
                        .bodyValue(Collections.singletonMap("error", error.getMessage())))
                .onErrorResume(AmountNotNullException.class, error -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .bodyValue(Collections.singletonMap("error", error.getMessage())))
                .onErrorResume(EmailNotNullException.class, error -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .bodyValue(Collections.singletonMap("error", error.getMessage())));


    }

   }


