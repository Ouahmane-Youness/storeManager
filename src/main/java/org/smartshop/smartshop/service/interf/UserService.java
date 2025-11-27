package org.smartshop.smartshop.service.interf;

import org.smartshop.smartshop.dto.userdto.CreateUserRequestDTO;
import org.smartshop.smartshop.dto.userdto.UserResponseDTO;

import java.util.List;

public interface UserService {

    UserResponseDTO createUser(CreateUserRequestDTO dto);

    UserResponseDTO findById(Long id);

    UserResponseDTO findByUsername(String username);

    List<UserResponseDTO> findAll();

    void deleteUser(Long id);
}
