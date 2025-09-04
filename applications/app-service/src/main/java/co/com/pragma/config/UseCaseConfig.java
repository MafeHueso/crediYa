package co.com.pragma.config;

import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.LogInUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {



    @Bean
    public LogInUseCase logInUseCase(UserRepository userRepository) {
        return new LogInUseCase(userRepository);
    }
}