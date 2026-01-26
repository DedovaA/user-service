package user_service.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import user_service.controller.UserController;
import user_service.dto.UserResponse;
import user_service.model.User;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserResponse, EntityModel<UserResponse>> {

    @Override
    public EntityModel<UserResponse> toModel(UserResponse userResponse) {
        // 1. Преобразуем сущность БД в наш Record
//        UserResponse user = new UserResponse(
//                userResponse.getId(),
//                userResponse.getName(),
//                userResponse.getEmail(),
//                userResponse.getAge(),
//                userResponse.getCreatedAt()
//        );

        // 2. Создаем EntityModel и добавляем ссылки
        return EntityModel.of(userResponse,
                linkTo(methodOn(UserController.class).getById(userResponse.getId())).withSelfRel(),

                linkTo(methodOn(UserController.class).getAll()).withRel("all_users"),

                linkTo(methodOn(UserController.class).getByEmail(userResponse.getEmail())).withRel("find_by_email"),

                linkTo(methodOn(UserController.class).update(userResponse.getId(), null)).withRel("update"),

                linkTo(methodOn(UserController.class).patch(userResponse.getId(), null)).withRel("patch"),

                linkTo(methodOn(UserController.class).delete(userResponse.getId())).withRel("delete")
        );
    }
}
