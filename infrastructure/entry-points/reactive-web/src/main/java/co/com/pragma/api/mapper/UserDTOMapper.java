package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.RegisterUserDTO;
import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {

    UserDTO toResponse(User user);
    User toModel(RegisterUserDTO registerUserDTO);


}
