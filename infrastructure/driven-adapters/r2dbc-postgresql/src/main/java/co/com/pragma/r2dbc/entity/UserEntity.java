package co.com.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    @Column("user_id")
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

