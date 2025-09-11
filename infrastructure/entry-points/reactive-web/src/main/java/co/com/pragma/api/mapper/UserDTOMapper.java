package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.api.dto.UserFindByDTO;
import co.com.pragma.model.user.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {

    UserDTO toResponse(User user);
    User toModel(UserDTO userDTO);

    UserFindByDTO toDomain(User user);
    User toDto(UserFindByDTO dto);
}
