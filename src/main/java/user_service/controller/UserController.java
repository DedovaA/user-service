package user_service.controller;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import user_service.assembler.UserModelAssembler;
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
    private final UserModelAssembler assembler;

    @Operation(summary = "Создать пользователя")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<UserResponse> create(@Valid @RequestBody UserCreateRequest request) { // @RequestBody JSON из body превратит в DTO. @Valid проверит данные согласно аннотациям в DTO
        return assembler.toModel(userService.create(request));
    }

    @Operation(summary = "Получить пользователя по ID")
    @GetMapping("/{id}")
    public EntityModel<UserResponse> getById(@PathVariable Long id) {
        return assembler.toModel(userService.getById(id));
    }

    @Operation(summary = "Получить пользователя по email")
    @GetMapping(params = "email")
    public EntityModel<UserResponse> getByEmail(@Valid @RequestParam String email) {
        return assembler.toModel(userService.getByEmail(email));
    }

    @Operation(summary = "Получить всех пользователей")
    @GetMapping
    public CollectionModel<EntityModel<UserResponse>> getAll() {
        return assembler.toCollectionModel(userService.getAll());
    }

    @Operation(summary = "Полностью обновить пользователя")
    @PutMapping("/{id}")
    public EntityModel<UserResponse> update(@PathVariable Long id,
                               @Valid @RequestBody UserCreateRequest request) {
        return assembler.toModel(userService.update(id, request));
    }

    @Operation(summary = "Частично обновить пользователя")
    @PatchMapping("/{id}")
    public EntityModel<UserResponse> patch(@PathVariable Long id,
                              @Valid @RequestBody UserPatchRequest request) {
        return assembler.toModel(userService.patch(id, request));
    }

    @Operation(summary = "Удалить пользователя")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
