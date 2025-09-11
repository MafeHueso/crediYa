package co.com.pragma.r2dbc.mapper;


import co.com.pragma.model.pagination.LoanPaginationFinal;
import co.com.pragma.r2dbc.loanApplication.PendingLoanResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PendingLoanMapper {
    LoanPaginationFinal toLoanPaginationFinal(PendingLoanResponseDTO dto);

    List<LoanPaginationFinal> toLoanPaginationFinalList(List<PendingLoanResponseDTO> dtoList);
}
