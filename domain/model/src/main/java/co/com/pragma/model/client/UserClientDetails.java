package co.com.pragma.model.client;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserClientDetails {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Long identificationNumber;
    private String phoneNumber;
    private Integer roleId;
    private BigDecimal salaryBase;
    private LocalDate dateBirthday;

}
