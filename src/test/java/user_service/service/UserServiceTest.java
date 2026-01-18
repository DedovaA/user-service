package user_service.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import user_service.dto.UserCreateRequest;
import user_service.dto.UserPatchRequest;
import user_service.dto.UserResponse;
import user_service.exception.BadRequestException;
import user_service.exception.NotFoundException;
import user_service.mapper.UserMapper;
import user_service.model.User;
import user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    UserService userService;

    private User user;
    private User patchedUser;
    private UserCreateRequest userCreateRequest;
    private UserCreateRequest userUpdateRequest;
    private UserPatchRequest userPatchRequest;
    private UserResponse response;
    private UserResponse patchedUserResponse;

    @BeforeEach
    void setUp() {
        LocalDateTime createdAt = LocalDateTime.now();
        user = new User(1L, "test", "test@example.com", 100, createdAt);
        patchedUser = new User(1L, "test", "test@example.com", 25, createdAt);

        userCreateRequest = new UserCreateRequest();
            userCreateRequest.setName("test");
            userCreateRequest.setEmail("test@example.com");
            userCreateRequest.setAge(100);

        userUpdateRequest = new UserCreateRequest();
            userUpdateRequest.setName("test");
            userUpdateRequest.setEmail("test@example.com");
            userUpdateRequest.setAge(100);

        userPatchRequest = new UserPatchRequest();
            userPatchRequest.setAge(25);

        response = new UserResponse(1L, "test", "test@example.com", 100, createdAt);
        patchedUserResponse = new UserResponse(1L, "test", "test@example.com", 25, createdAt);
    }

    @AfterEach
    void tearDown() {
    }

    @DisplayName("Должен сохранить пользователя в БД, и вернуть ответ.")
    @Test
    void create_shouldSaveAndReturnResponse() {
        when(userMapper.toEntity(userCreateRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse savedUser = userService.create(userCreateRequest);

        assertSame(response, savedUser);
        verify(userMapper, times(1)).toEntity(userCreateRequest);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toResponse(user);
    }

    @DisplayName("Должен бросить BadRequestException, если email не уникален.")
    @Test
    void create_shouldThrowBadRequest_whenDuplicateEmail() {
        when(userMapper.toEntity(userCreateRequest)).thenReturn(user);
        doThrow(new DataIntegrityViolationException("duplicate email"))
                .when(userRepository).save(user);

        assertThrows(BadRequestException.class,() -> userService.create(userCreateRequest));
        verify(userMapper, times(1)).toEntity(userCreateRequest);
        verify(userRepository, times(1)).save(user);
    }

    @DisplayName("Должен вернуть пользователя по id из БД.")
    @Test
    void getById_shouldReturnUserResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse foundUser = userService.getById(1L);

        assertSame(response, foundUser);
        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).toResponse(user);
    }

    @DisplayName("Должен бросить NotFoundException, если пользователь по id не найден.")
    @Test
    void getById_shouldThrowNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,() -> userService.getById(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @DisplayName("Должен вернуть список всех пользователей из БД.")
    @Test
    void getAll_shouldReturnMappedList() {
        List<User> userList = Arrays.asList(user, user);
        List<UserResponse> responseList = Arrays.asList(response, response);

        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.toResponse(user)).thenReturn(response);

        List<UserResponse> foundList = userService.getAll();

        assertIterableEquals(responseList, foundList);
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(2)).toResponse(user);

    }

    @DisplayName("Должен обновить пользователя и вернуть ответ.")
    @Test
    void update_shouldUpdateAndReturnResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse updatedUser = userService.update(1L, userUpdateRequest);

        assertSame(response, updatedUser);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toResponse(user);
    }

    @DisplayName("Должен бросить BadRequestException, при попытке задать пользователю неуникальный(чужой) email.")
    @Test
    void update_shouldThrowBadRequest_whenDuplicateEmail() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doThrow(new DataIntegrityViolationException("duplicate email"))
                .when(userRepository).save(user);

        assertThrows(BadRequestException.class,() -> userService.update(1L, userUpdateRequest));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @DisplayName("Должен бросить NotFoundException, при попытке обновления, если пользователь не найден.")
    @Test
    void update_shouldThrowNotFoundException() {
        doThrow(new NotFoundException("not found")).when(userRepository).findById(1L);

        assertThrows(NotFoundException.class,() -> userService.update(1L, userUpdateRequest));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(user);
    }

    @DisplayName("Должен обновить выбранные поля и вернуть ответ.")
    @Test
    void patch_shouldUpdateOnlyProvidedFields() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(userRepository.save(user)).thenReturn(patchedUser);
        when(userMapper.toResponse(patchedUser)).thenReturn(patchedUserResponse);

        UserResponse updatedUserResponse = userService.patch(1L, userPatchRequest);

        assertEquals(patchedUserResponse.getAge(), updatedUserResponse.getAge());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toResponse(patchedUser);
    }

    @DisplayName("Должен бросить BadRequestException, при попытке задать пользователю неуникальный(чужой) email.")
    @Test
    void patch_shouldThrowBadRequest_whenDuplicateEmail() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doThrow(new DataIntegrityViolationException("duplicate email"))
                .when(userRepository).save(user);

        assertThrows(BadRequestException.class,() -> userService.patch(1L, userPatchRequest));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @DisplayName("Должен бросить NotFoundException, при попытке обновления, если пользователь не найден.")
    @Test
    void patch_shouldThrowNotFoundException() {

        doThrow(new NotFoundException("not found")).when(userRepository).findById(1L);

        assertThrows(NotFoundException.class,() -> userService.patch(1L, userPatchRequest));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(user);
    }

    @DisplayName("Должен удалить пользователя из БД.")
    @Test
    void delete_shouldCallRepositoryDeleteById() {
        doNothing().when(userRepository).deleteById(1L);

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @DisplayName("Должен вернуть пользователя по email из БД.")
    @Test
    void getByEmail_shouldReturnUserResponse() {
        when(userRepository.findByEmail("test@email.ru")).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse foundUser = userService.getByEmail("test@email.ru");

        assertSame(response, foundUser);
        verify(userRepository, times(1)).findByEmail("test@email.ru");
        verify(userMapper, times(1)).toResponse(user);
    }

    @DisplayName("Должен бросить NotFoundException, если пользователь по email не найден.")
    @Test
    void getByEmail_shouldThrowNotFoundException() {
        when(userRepository.findByEmail("test@email.ru")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,() -> userService.getByEmail("test@email.ru"));
        verify(userRepository, times(1)).findByEmail("test@email.ru");
    }
}