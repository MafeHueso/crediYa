package co.com.pragma.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "routes.paths")
public class LoanPath {
    private String createLoan;
    private String findByTypeLoan;
    private String findByIdentificationAndLoanType;
    private String pendingLoans;

}
