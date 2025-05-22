package am.itspace.bookshop.mapper;

import am.itspace.bookshop.dto.SaveUserRequest;
import am.itspace.bookshop.dto.UserUpdateResponse;
import am.itspace.bookshop.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(SaveUserRequest saveUserRequest);

    UserUpdateResponse toDto(User user);
}