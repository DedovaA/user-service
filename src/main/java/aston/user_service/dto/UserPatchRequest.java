package user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Запрос на частичное обновление данных пользователя")
public class UserPatchRequest {
    @Schema(description = "Новое имя пользователя", example = "Ivan Updated", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;

    @Schema(description = "Новая электронная почта", example = "new@example.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Email(message = "email must be valid")
    private String email;

    @Schema(description = "Новый возраст", example = "30", minimum = "0", maximum = "150", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Min(value = 0, message = "age must be >= 0")
    @Max(value = 150, message = "age must be <= 150")
    private Integer age;
}
