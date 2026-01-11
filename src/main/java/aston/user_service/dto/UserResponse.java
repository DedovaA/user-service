package aston.user_service.dto;

import lombok.*;

@Value
public class UserResponse {
    Long id;
    String name;
    String email;
}
