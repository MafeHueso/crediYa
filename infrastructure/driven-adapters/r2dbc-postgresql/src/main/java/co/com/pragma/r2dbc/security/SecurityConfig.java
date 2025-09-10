package co.com.pragma.r2dbc.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;



@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    public SecurityConfig(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public JwtAuthenticationManager jwtAuthenticationManager() {
        return new JwtAuthenticationManager(jwtProvider);
    }

    @Bean
    public SecurityContextRepository securityContextRepository(JwtAuthenticationManager jwtAuthenticationManager) {
        return new SecurityContextRepository(jwtAuthenticationManager);
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, SecurityContextRepository securityContextRepository) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/v1/pending").hasRole("ADMIN")
                        .pathMatchers("/api/v1/application").hasRole("CLIENT")
                        .anyExchange().authenticated()
                )
                .securityContextRepository(securityContextRepository)
                .build();
    }

}