package org.smartshop.smartshop.mapper.usermapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.smartshop.smartshop.dto.userdto.CreateUserRequestDTO;
import org.smartshop.smartshop.dto.userdto.UserResponseDTO;
import org.smartshop.smartshop.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toResponseDTO(User user);

    List<UserResponseDTO> toResponseDTOList(List<User> users);

    @Mapping(target = "client", ignore = true)
    User toEntity(CreateUserRequestDTO dto);

}