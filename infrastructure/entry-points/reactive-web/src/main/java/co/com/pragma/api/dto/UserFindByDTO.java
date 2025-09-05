package co.com.pragma.api.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserFindByDTO {


    private String firstName;

    private String lastName;

    private String email;

    private Long identificationNumber;

    private String phoneNumber;

    private String roleId;

    private BigDecimal salaryBase;

}

