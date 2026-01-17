package aston.user_service.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserPatchRequest {
    private String name;

    @Email(message = "email must be valid")
    private String email;

    @Min(value = 0, message = "age must be >= 0")
    @Max(value = 150, message = "age must be <= 0")
    private Integer age;
}
