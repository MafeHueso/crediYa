package co.com.pragma.api;

import co.com.pragma.api.config.LoanPath;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {
    private final LoanPath loanPath;
    private final Handler loanHandler;
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(loanPath.getCreateLoan()), loanHandler::listenSaveLoan);
               // .andRoute(GET(loanPath.getFindByIdentificationAndLoanType()), loanHandler::listenFindByIdentificationAndLoanType);
    }
}
