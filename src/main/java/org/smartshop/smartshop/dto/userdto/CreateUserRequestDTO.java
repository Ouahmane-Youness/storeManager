package org.smartshop.smartshop.dto.userdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.smartshop.smartshop.enums.UserRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequestDTO {

    @NotBlank(message = "username is required")
    @Size(min = 3, max = 50, message = "userName must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "password is required")
    @Size(min = 6, message = "password must be atleast 6 characters")
    private String password;

    @NotNull(message = "Role is required")
    private UserRole role;
}
