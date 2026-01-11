package aston.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserCreateRequest {
    @NotBlank(message = "name must not be blank")
    private String name;

    @Email(message = "email must be valid")
    @NotBlank(message = "email must not be blank")
    private String email;
}
