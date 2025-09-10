package co.com.pragma.api.mapper;

import co.com.pragma.api.saveApplication.LoanApplicationRequestDTO;
import co.com.pragma.api.saveApplication.LoanApplicationResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapper {
    LoanApplicationResponseDTO toResponseDTO(LoanApplicationRequestDTO requestDTO);

    // Mapeo de Response a Request (si es necesario)
    LoanApplicationRequestDTO toRequestDTO(LoanApplicationResponseDTO responseDTO);

}
