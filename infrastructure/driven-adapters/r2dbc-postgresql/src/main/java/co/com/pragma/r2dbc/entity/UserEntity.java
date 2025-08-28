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
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    private String email;
    @Column("identification_number")
    private Long identificationNumber;
    @Column("phone_number")
    private String phoneNumber;
    @Column("role_id")
    private Integer roleId;
    @Column("salary_base")
    private BigDecimal salaryBase;
    @Column("date_birthday")
    private LocalDate dateBirthday;
}

