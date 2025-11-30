package org.smartshop.smartshop.service.interf;

import jakarta.servlet.http.HttpSession;
import org.smartshop.smartshop.dto.auth.LoginRequestDTO;
import org.smartshop.smartshop.dto.auth.LoginResponseDTO;

public interface AuthService {


    LoginResponseDTO login(LoginRequestDTO dto, HttpSession session);

    void logout(HttpSession session);

    boolean isAuthenticated(HttpSession session);

    boolean isAdmin(HttpSession session);
}
