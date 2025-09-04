/*package co.com.pragma.api;

import co.com.pragma.api.path.LoginPath;
import co.com.pragma.api.path.UserPath;

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

    private final UserPath userPath;
    private final UserHandler userHandler;
    @Bean
    public RouterFunction<ServerResponse> userFunction(UserHandler handler) {
        return route(POST(userPath.getUsers()), userHandler::listenSaveUser)
                .andRoute(GET(userPath.getUsers() + "/{email}"), userHandler::listenFindByEmail);
    }
}
*/