/*package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.document.UserLogin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserLoginMapper {
     @Mapping(target = "userId", ignore = true)
     @Mapping(target = "status", constant = "true")
     UserLogin toEntity(User user);
     User toDomain(UserLogin userLogin);
}


*/