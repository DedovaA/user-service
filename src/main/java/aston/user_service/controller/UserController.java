package user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import user_service.hateoas.UserModelAssembler;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Операции с пользователями")
public class UserController {

    private final UserService userService;
    private final UserModelAssembler assembler;

    @Operation(summary = "Создать пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return assembler.toModel(userService.create(request));
    }

    @Operation(summary = "Получить пользователя по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public EntityModel<UserResponse> getById(@PathVariable Long id) {
        return assembler.toModel(userService.getById(id));
    }

    @Operation(summary = "Получить пользователя по email")
//    @GetMapping(params = "email")
//    public EntityModel<UserResponse> getByEmail(@Valid @RequestParam String email) {
//        return assembler.toModel(userService.getByEmail(email));
//    }
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/email")
    public EntityModel<UserResponse> getByEmail(@RequestParam String email) {
        return assembler.toModel(userService.getByEmail(email));
    }

    @Operation(summary = "Получить всех пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользователей получен")
    @GetMapping
    public CollectionModel<EntityModel<UserResponse>> getAll() {
        List<UserResponse> users = userService.getAll();
        return assembler.toCollectionModel(users);
    }

    @Operation(summary = "Полностью обновить пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные обновлены"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping("/{id}")
    public EntityModel<UserResponse> update(@PathVariable Long id,
                               @Valid @RequestBody UserCreateRequest request) {
        return assembler.toModel(userService.update(id, request));
    }

    @Operation(summary = "Частично обновить пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные частично обновлены"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PatchMapping("/{id}")
    public EntityModel<UserResponse> patch(@PathVariable Long id,
                              @Valid @RequestBody UserPatchRequest request) {
        return assembler.toModel(userService.patch(id, request));
    }

    @Operation(summary = "Удалить пользователя")
    @ApiResponse(responseCode = "204", description = "Пользователь удален")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
