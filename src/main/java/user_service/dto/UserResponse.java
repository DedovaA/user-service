package user_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Value
public class UserResponse {
    Long id;
    String name;
    String email;
    Integer age;
    LocalDateTime createdAt;
}
