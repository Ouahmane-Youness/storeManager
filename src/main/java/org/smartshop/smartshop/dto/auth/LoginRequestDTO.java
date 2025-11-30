package org.smartshop.smartshop.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequestDTO {


    @NotBlank(message = "username cannot be empty")
    private String username;

    @NotBlank(message = "password is required")
    private String password;
}
