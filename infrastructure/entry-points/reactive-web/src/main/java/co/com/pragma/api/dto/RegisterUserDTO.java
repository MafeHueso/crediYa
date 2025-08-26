package co.com.pragma.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegisterUserDTO (
    Long id,
    String firstName,
    String lastName,
    String email,
    Long identificationNumber,
    String phoneNumber,
    Integer roleId,
    BigDecimal salaryBase,
    LocalDate dateBirthday
) {}