/*package co.com.pragma.r2dbc.document;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Stream;

@Table(name = "user_login")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserLogin implements UserDetails {
    @Id
    @Column(name="user_id")
    private Long userId;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="email")
    private String email;

    @Column(name="identification_number")
    private Long identificationNumber;

    @Column(name="phone_number")
    private String phoneNumber;

    @Column(name="role_id")
    private String roleId;

    @Column(name="salary_base")
    private BigDecimal salaryBase;

    @Column(name="date_birthday")
    private LocalDate dateBirthday;

    private String password;
    private Boolean status;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.of(roleId.split(", ")).map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return status;
    }

    @Override
    public boolean isEnabled() {
        return status;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", identificationNumber=" + identificationNumber +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", roleId='" + roleId + '\'' +
                ", salaryBase=" + salaryBase +
                ", dateBirthday=" + dateBirthday +
                ", status=" + status +

                '}';
    }
}
*/