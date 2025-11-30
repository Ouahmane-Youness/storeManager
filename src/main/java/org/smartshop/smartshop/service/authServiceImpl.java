package org.smartshop.smartshop.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.smartshop.smartshop.dto.auth.LoginRequestDTO;
import org.smartshop.smartshop.dto.auth.LoginResponseDTO;
import org.smartshop.smartshop.entity.User;
import org.smartshop.smartshop.enums.UserRole;
import org.smartshop.smartshop.exception.BusinessException;
import org.smartshop.smartshop.exception.ResourceNotFoundException;
import org.smartshop.smartshop.repository.UserRepository;
import org.smartshop.smartshop.service.interf.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class authServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto, HttpSession session) {
        User user = userRepository.findByUsernameAndDeletedFalse(dto.getUsername())
                .orElseThrow(() -> new BusinessException("Invalid username or password"));

        if (!user.getPassword().equals(dto.getPassword())) {
            throw new BusinessException("Invalid username or password");
        }

        session.setAttribute("userId", user.getId());
        session.setAttribute("userName", user.getUsername());
        session.setAttribute("userRole", user.getRole());

        return LoginResponseDTO.builder()
                .userid(user.getId())
                .username(user.getUsername())
                .userRole(user.getRole())
                .message("Login successful")
                .build();
    }

    @Override
    public void logout(HttpSession session) {

        session.invalidate();
    }

    @Override
    public boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("userName") != null;
    }

    @Override
    public boolean isAdmin(HttpSession session) {
        return session.getAttribute("userRole").equals(UserRole.ADMIN);
    }
}
