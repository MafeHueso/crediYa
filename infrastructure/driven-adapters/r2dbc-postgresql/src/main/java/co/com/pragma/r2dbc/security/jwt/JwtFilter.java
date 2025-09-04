package co.com.pragma.r2dbc.security.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtFilter implements WebFilter {

    private static final List<String> PUBLIC_PATHS = List.of(

            "/api/v1/login/login",
            "/api/v1/login/signup"
    );
    private final JwtProvider jwtProvider;

    public JwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(path::startsWith);

        if (isPublic) {
            return chain.filter(exchange);
        }
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.error(new RuntimeException("no token was found or invalid auth header"));
        }

        String token = authHeader.substring(7);

        if (!jwtProvider.validate(token)) {
            return Mono.error(new RuntimeException("invalid or expired token"));
        }

        exchange.getAttributes().put("token", token);

        return chain.filter(exchange);
    }
}
