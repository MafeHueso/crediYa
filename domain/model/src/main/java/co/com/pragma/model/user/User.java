package co.com.pragma.model.user;


import java.math.BigDecimal;
import java.time.LocalDate;

public record User (

        Long userId,
        String firstName,
        String lastName,
        String email,
        Long identificationNumber,
        String phoneNumber,
        String roleId,
        BigDecimal salaryBase,
        LocalDate dateBirthday,
        String password,
        Boolean status
){}
