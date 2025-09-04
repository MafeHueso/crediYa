package co.com.pragma.api;

import co.com.pragma.api.path.LoginPath;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class LoginRouterRest {
    private final LoginHandler loginHandler;
    private final LoginPath loginPath;
    @Bean
    public RouterFunction<ServerResponse> loginFunction(LoginHandler handler) {
        return route(POST(loginPath.getLogin() + "/signup"), loginHandler::signUp)
                .andRoute(POST(loginPath.getLogin() + "/login"), loginHandler::logIn);
    }
}

