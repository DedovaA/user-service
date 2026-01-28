package user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Value
@Schema(description = "Информация о пользователе")
public class UserResponse {
    @Schema(description = "ID пользователя", example = "1")
    Long id;
    @Schema(description = "Имя", example = "Ivan")
    String name;
    @Schema(description = "Электронная почта", example = "ivan@example.com")
    String email;
    @Schema(description = "Возраст", example = "25")
    Integer age;
    @Schema(description = "Дата создания")
    LocalDateTime createdAt;
}
