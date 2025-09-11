package co.com.pragma.api;

import co.com.pragma.api.path.LoginPath;
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
public class UserRouterRest {
    private final UserHandler userHandler;
    private final LoginPath loginPath;
    @Bean
    public RouterFunction<ServerResponse> loginFunction(UserHandler handler) {
        return route(POST(loginPath.getSignUp()), userHandler::signUp)
                .andRoute(POST(loginPath.getLogin()), userHandler::logIn)
                .andRoute(GET(loginPath.getUsersByEmail()), userHandler::findByEmail)
                .andRoute(GET(loginPath.getUsersByIdentificationNumber()), userHandler::findByIdentificationNumber);

    }
}

