package user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import user_service.dto.UserCreateRequest;
import user_service.dto.UserPatchRequest;
import user_service.dto.UserResponse;
import user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Операции с пользователями")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Создать пользователя")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserCreateRequest request) { // @RequestBody JSON из body превратит в DTO. @Valid проверит данные согласно аннотациям в DTO
        return userService.create(request);
    }

    @Operation(summary = "Получить пользователя по ID")
    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @Operation(summary = "Получить пользователя по email")
    @GetMapping(params = "email")
    public UserResponse getByEmail(@Valid @RequestParam String email) {
        return userService.getByEmail(email);
    }

    @Operation(summary = "Получить всех пользователей")
    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAll();
    }

    @Operation(summary = "Полностью обновить пользователя")
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id,
                               @Valid @RequestBody UserCreateRequest request) {
        return userService.update(id, request);
    }

    @Operation(summary = "Частично обновить пользователя")
    @PatchMapping("/{id}")
    public UserResponse patch(@PathVariable Long id,
                               @Valid @RequestBody UserPatchRequest request) {
        return userService.patch(id, request);
    }

    @Operation(summary = "Удалить пользователя")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
