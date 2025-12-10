package org.smartshop.smartshop.dto.auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.smartshop.smartshop.enums.UserRole;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginResponseDTO {

    private long userid;
    private String username;
    private UserRole userRole;
    private String message;

}
