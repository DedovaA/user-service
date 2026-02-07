package user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Запрос на создание нового пользователя")
public class UserCreateRequest {
    @Schema(description = "Имя пользователя", example = "Ivan", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "name must not be blank")
    private String name;

    @Schema(description = "Электронная почта", example = "ivan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email(message = "email must be valid")
    @NotBlank(message = "email must not be blank")
    private String email;

    @Schema(description = "Возраст", example = "25", minimum = "0", maximum = "150", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "age must not be null")
    @Min(value = 0, message = "age must be >= 0")
    @Max(value = 150, message = "age must be <= 0")
    private Integer age;

}
