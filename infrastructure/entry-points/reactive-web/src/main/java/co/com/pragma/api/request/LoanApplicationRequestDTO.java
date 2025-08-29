package co.com.pragma.api.request;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoanApplicationRequestDTO {
    @NotNull
    @NotBlank(message = "Field identification number cannot be null or empty")
    private String identificationNumber;
    @NotNull
    @NotBlank(message = "Field amount cannot be null or empty")
    private BigDecimal amount;
    @NotNull
    @NotBlank(message = "Field term months cannot be null or empty")
    private Integer termMonths;
    @NotNull
    @NotBlank(message = "Field loan type id cannot be null or empty")
    private Long loanTypeId;
}
