package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.r2dbc.client.ClientResponseDTO;
import co.com.pragma.r2dbc.entity.LoanApplicationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanApplicationEntityMapper {
    LoanApplication toModel(LoanApplicationEntity entity);

    LoanApplicationEntity toEntity(LoanApplication model);
    ClientResponseDTO toDto(LoanApplication domain);


}
