package co.com.pragma.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDTO{

    private Long user_id;

    @NotNull
    @NotBlank(message = "Field firstname cannot be null or empty")
    private String firstName;

    @NotNull
    @NotBlank(message = "Field lastname cannot be null or empty")
    private String lastName;

    @NotNull
    @NotBlank(message = "Field email cannot be null or empty")
    @Email(message = "Field email must be a valid email address")
    private String email;

    private Long identificationNumber;

    private String phoneNumber;

    private String roleId;

    @NotNull(message = "Field salary cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Salary must be greater than or equal to 0")
    @DecimalMax(value = "15000000.0", inclusive = true, message = "Salary must be less than or equal to 15,000,000")
    private BigDecimal salaryBase;

    private LocalDate dateBirthday;

    @NotNull(message = "Field password cannot be null")
    private String password;


}