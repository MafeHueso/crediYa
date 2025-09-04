package co.com.pragma.r2dbc.security.jwt;


import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationManager(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials() == null ? "" : authentication.getCredentials().toString();

        if (token.isEmpty()) {
            // No hay token: no autenticamos, dejamos que siga la cadena sin error
            return Mono.empty();
        }

        return Mono.just(token)
                .flatMap(t -> {
                    try {
                        Claims claims = jwtProvider.getClaims(t);

                        List<SimpleGrantedAuthority> authorities = Stream.of(claims.get("roleId"))
                                .map(role -> (List<Map<String, String>>) role)
                                .flatMap(roleList -> roleList.stream()
                                        .map(r -> r.get("authority"))
                                        .map(SimpleGrantedAuthority::new))
                                .toList();

                        Authentication auth = new UsernamePasswordAuthenticationToken(
                                claims.getSubject(),
                                null,
                                authorities
                        );

                        return Mono.just(auth);

                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("bad token"));
                    }
                });
    }
}