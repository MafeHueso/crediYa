package co.com.pragma.model.user;


import java.math.BigDecimal;
import java.time.LocalDate;

public record User (

        Long id,
        String firstName,
        String lastName,
        String email,
        Long identificationNumber,
        String phoneNumber,
        Integer roleId,
        BigDecimal salaryBase,
        LocalDate dateBirthday
){}
