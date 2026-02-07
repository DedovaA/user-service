package aston.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import user_service.controller.UserController;
import user_service.dto.UserCreateRequest;
import user_service.dto.UserPatchRequest;
import user_service.dto.UserResponse;
import user_service.hateoas.UserModelAssembler;
import user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Spy
    private UserModelAssembler assembler = new UserModelAssembler();

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private UserResponse response;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        response = new UserResponse(1L, "Ivan", "ivan@example.com", 25, LocalDateTime.now());
    }

    @Test
    @DisplayName("POST /api/users - Успешное создание")
    void create_ShouldReturn201() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("Ivan");
        request.setEmail("ivan@example.com");
        request.setAge(25);

        when(userService.create(any(UserCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("ivan@example.com"));

        verify(userService).create(any(UserCreateRequest.class));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Получение по ID")
    void getById_ShouldReturnUser() throws Exception {
        when(userService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.links[?(@.rel == 'self')].href").exists());

        verify(userService).getById(1L);
    }

    @Test
    @DisplayName("GET /api/users/email - Получение по Email")
    void getByEmail_ShouldReturnUser() throws Exception {
        when(userService.getByEmail("ivan@example.com")).thenReturn(response);

        mockMvc.perform(get("/api/users/email")
                        .param("email", "ivan@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ivan@example.com"));

        verify(userService).getByEmail("ivan@example.com");
    }

    @Test
    @DisplayName("GET /api/users - Получение всех")
    void getAll_ShouldReturnCollection() throws Exception {
        when(userService.getAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].email").value("ivan@example.com"));

        verify(userService).getAll();
    }

    @Test
    @DisplayName("PATCH /api/users/{id} - Частичное обновление")
    void patch_ShouldReturnUpdatedUser() throws Exception {
        UserPatchRequest patch = new UserPatchRequest();
        patch.setAge(30);

        UserResponse updatedResponse = new UserResponse(1L, "Ivan", "ivan@example.com", 30, LocalDateTime.now());
        when(userService.patch(eq(1L), any(UserPatchRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(patch("/api/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age").value(30));

        verify(userService).patch(eq(1L), any(UserPatchRequest.class));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Удаление")
    void delete_ShouldReturn204() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(userService).delete(1L);
    }
}
