package co.com.pragma.api.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoanApplicationResponseDTO {
    private Long applicationId;
    private Integer statusId;
    private String message;

}
