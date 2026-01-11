package aston.user_service.mapper;

import aston.user_service.dto.UserCreateRequest;
import aston.user_service.dto.UserResponse;
import aston.user_service.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserCreateRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .age(request.getAge())
                .build();
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}
