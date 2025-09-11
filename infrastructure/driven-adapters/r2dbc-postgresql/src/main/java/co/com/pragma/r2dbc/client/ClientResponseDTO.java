package co.com.pragma.r2dbc.client;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ClientResponseDTO {

    private String firstName;
    private String lastName;
    private String email;
    private Long identificationNumber;
    private String phoneNumber;
    private String roleId;
    private BigDecimal salaryBase;
}
