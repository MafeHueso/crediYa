package co.com.pragma.api;

import co.com.pragma.api.mapper.LoanApplicationMapper;
import co.com.pragma.api.request.LoanApplicationRequestDTO;
import co.com.pragma.api.response.LoanApplicationResponseDTO;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.usecase.loanapplication.LoanApplicationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class Handler {

    private final LoanApplicationUseCase loanUseCase;
    private final LoanApplicationMapper loanApplicationMapper;


    public Mono<ServerResponse> listenSaveLoan(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(LoanApplicationRequestDTO.class)
                .flatMap(req -> {
                    return loanUseCase.findByTypeLoan(req.getLoanTypeId())
                            .flatMap(loanType -> {
                                LoanApplication loanApplication = new LoanApplication(
                                        null,
                                        req.getIdentificationNumber(),
                                        req.getAmount(),
                                        req.getTermMonths(),
                                        1,
                                        req.getLoanTypeId(),

                                        OffsetDateTime.now()
                                );
                                return loanUseCase.saveLoan(loanApplication);
                            })
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Loan type invalid")));
                })
                .flatMap(loanApplication -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(LoanApplicationResponseDTO.builder()
                                .applicationId(loanApplication.applicationId())
                                .statusId(loanApplication.statusId())
                                .message("Pending review")
                                .build()));
    }




}
