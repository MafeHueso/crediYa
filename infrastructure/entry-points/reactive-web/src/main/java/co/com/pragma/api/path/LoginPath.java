package co.com.pragma.api.path;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "routes.paths")
public class LoginPath {
    private String login;
    private String signUp;
    private String usersByEmail;
    private String usersByIdentificationNumber;
}
