package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.r2dbc.entity.LoanApplicationEntity;
import co.com.pragma.r2dbc.entity.LoanTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanApplicationEntityMapper {
    LoanApplicationEntity toEntityAp(LoanApplication loanApplication);
    LoanApplication toModelAp(LoanApplicationEntity entityApp);
}
