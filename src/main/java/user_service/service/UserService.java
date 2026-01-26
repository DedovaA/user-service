package user_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import user_service.dto.UserCreateRequest;
import user_service.dto.UserPatchRequest;
import user_service.dto.UserResponse;
import user_service.exception.BadRequestException;
import user_service.exception.NotFoundException;
import user_service.kafka.UserEvent;
import user_service.kafka.UserEventProducer;
import user_service.mapper.UserMapper;
import user_service.model.User;
import user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    //для кафки
    private final UserEventProducer userEventProducer;

    public UserResponse create(UserCreateRequest request) {
        logger.info("Попытка регистрации нового пользователя с email: {}", request.getEmail());
        User user = userMapper.toEntity(request);

        try {
            User saved = userRepository.save(user);
            logger.debug("Пользователь успешно сохранен в БД с ID: {}", saved.getId());

            userEventProducer.send(new UserEvent(UserEvent.Operation.CREATE, saved.getEmail()));

            return userMapper.toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Отказ в регистрации: email {} уже занят", request.getEmail());
            throw new BadRequestException("User with email already exists: " + request.getEmail());
        }
    }

    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        return userMapper.toResponse(user);
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse update(Long id, UserCreateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());

        try {
            User updated = userRepository.save(user);
            return userMapper.toResponse(updated);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("User with email already exists: " + request.getEmail());
        }
    }

    public UserResponse patch(Long id, UserPatchRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        if (request.getName() != null)
            user.setName(request.getName().trim());
        if (request.getEmail() != null)
            user.setEmail(request.getEmail().trim());
        if (request.getAge() != null)
            user.setAge(request.getAge());

        try {
            User updated = userRepository.save(user);
            return userMapper.toResponse(updated);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("User with email already exists: " + request.getEmail());
        }
    }

    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        userRepository.deleteById(id);

        userEventProducer.send(new UserEvent(UserEvent.Operation.DELETE, user.getEmail()));
    }

    public UserResponse getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

}
