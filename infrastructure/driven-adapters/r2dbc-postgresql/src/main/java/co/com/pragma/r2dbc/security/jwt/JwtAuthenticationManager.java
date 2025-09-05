package co.com.pragma.r2dbc.security.jwt;


import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationManager(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials() == null ? "" : authentication.getCredentials().toString();
        log.info("Token enviado al login service: {}", token);
        if (token.isEmpty()) {
            return Mono.empty();
        }

        return Mono.just(token)
                .flatMap(t -> {
                    try {
                        Claims claims = jwtProvider.getClaims(t);

                        List<SimpleGrantedAuthority> authorities = Stream.of(claims.get("roleId"))
                                .map(role -> (List<Map<String, String>>) role)
                                .flatMap(roleList -> roleList.stream()
                                        .map(r -> "ROLE_" + r.get("authority"))
                                        .map(SimpleGrantedAuthority::new))
                                .toList();

                        Authentication auth = new UsernamePasswordAuthenticationToken(
                                claims.getSubject(),
                                null,
                                authorities
                        );

                        return Mono.just(auth);

                    } catch (Exception e) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad token");
                    }
                });
    }
}