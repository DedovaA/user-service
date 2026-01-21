package user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import user_service.dto.UserCreateRequest;
import user_service.dto.UserPatchRequest;
import user_service.dto.UserResponse;
import user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private UserService userService;

    private UserResponse sampleUser(long id) {
        return new UserResponse(
                id,
                "Ivan",
                "ivan@example.com",
                25,
                LocalDateTime.parse("2026-01-21T10:15:30")
        );
    }

    @Test
    void create_shouldReturn201_andBody() throws Exception {
        var req = new UserCreateRequest();
        req.setName("Ivan");
        req.setEmail("ivan@example.com");
        req.setAge(25);

        when(userService.create(ArgumentMatchers.any(UserCreateRequest.class)))
                .thenReturn(sampleUser(1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("ivan@example.com"))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(userService).create(ArgumentMatchers.any(UserCreateRequest.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void create_shouldReturn400_whenValidationFails() throws Exception {
        var req = new UserCreateRequest();
        req.setName("   ");
        req.setEmail("not-an-email");
        req.setAge(-1);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void getById_shouldReturn200_andBody() throws Exception {
        when(userService.getById(42L)).thenReturn(sampleUser(42));

        mockMvc.perform(get("/users/{id}", 42))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.email").value("ivan@example.com"));

        verify(userService).getById(42L);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getByEmail_shouldReturn200_andBody() throws Exception {
        when(userService.getByEmail("ivan@example.com")).thenReturn(sampleUser(7));

        mockMvc.perform(get("/users/email").param("email", "ivan@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.email").value("ivan@example.com"));

        verify(userService).getByEmail("ivan@example.com");
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getAll_shouldReturn200_andArray() throws Exception {
        when(userService.getAll()).thenReturn(List.of(sampleUser(1), sampleUser(2)));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(userService).getAll();
        verifyNoMoreInteractions(userService);
    }

    @Test
    void update_shouldReturn200_andBody() throws Exception {
        var req = new UserCreateRequest();
        req.setName("NewName");
        req.setEmail("new@example.com");
        req.setAge(30);

        when(userService.update(eq(10L), any(UserCreateRequest.class)))
                .thenReturn(new UserResponse(
                        10L, "NewName", "new@example.com", 30,
                        LocalDateTime.parse("2026-01-21T10:15:30")
                ));

        mockMvc.perform(put("/users/{id}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("NewName"))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.age").value(30));

        verify(userService).update(eq(10L), any(UserCreateRequest.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void update_shouldReturn400_whenValidationFails() throws Exception {
        var req = new UserCreateRequest();
        req.setName("");             // @NotBlank
        req.setEmail("bad");         // @Email
        req.setAge(999);             // @Max(150) (у тебя message сейчас "age must be <= 0" — опечатка)

        mockMvc.perform(put("/users/{id}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void patch_shouldReturn200_andBody() throws Exception {
        var req = new UserPatchRequest();
        req.setAge(40);

        when(userService.patch(eq(5L), any(UserPatchRequest.class)))
                .thenReturn(new UserResponse(
                        5L, "Ivan", "ivan@example.com", 40,
                        LocalDateTime.parse("2026-01-21T10:15:30")
                ));

        mockMvc.perform(patch("/users/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.age").value(40));

        verify(userService).patch(eq(5L), any(UserPatchRequest.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void patch_shouldReturn400_whenValidationFails() throws Exception {
        var req = new UserPatchRequest();
        req.setEmail("not-email"); // @Email

        mockMvc.perform(patch("/users/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        doNothing().when(userService).delete(99L);

        mockMvc.perform(delete("/users/{id}", 99))
                .andExpect(status().isNoContent());

        verify(userService).delete(99L);
        verifyNoMoreInteractions(userService);
    }
}