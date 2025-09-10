package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.client.UserClientDetails;
import co.com.pragma.r2dbc.client.ClientResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserClientDetails toDomain(ClientResponseDTO dto);
    ClientResponseDTO toDto(UserClientDetails domain);
}
