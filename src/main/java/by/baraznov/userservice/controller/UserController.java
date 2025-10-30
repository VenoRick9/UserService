package by.baraznov.userservice.controller;

import by.baraznov.userservice.dto.PageResponse;
import by.baraznov.userservice.dto.user.UserCreateDTO;
import by.baraznov.userservice.dto.user.UserGetDTO;
import by.baraznov.userservice.dto.user.UserUpdateDTO;
import by.baraznov.userservice.mapper.user.CreateDTOCreateCommandMapper;
import by.baraznov.userservice.mediator.Mediator;
import by.baraznov.userservice.read.query.GetAllUsersQuery;
import by.baraznov.userservice.read.query.GetUserByEmailQuery;
import by.baraznov.userservice.read.query.GetUserByIdQuery;
import by.baraznov.userservice.read.query.GetUsersByIdsQuery;
import by.baraznov.userservice.write.command.CreateUserCommand;
import by.baraznov.userservice.write.command.DeleteUserCommand;
import by.baraznov.userservice.write.command.UpdateUserCommand;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final Mediator mediator;
    private final CreateDTOCreateCommandMapper createDTOCreateCommandMapper;

    @GetMapping
    public ResponseEntity<PageResponse<UserGetDTO>> getAllUsers(
            @PageableDefault(page = 0, size = 10, sort = {"id"}) Pageable pageable) {
        return ResponseEntity.ok(PageResponse.toPageResponse(mediator.send(GetAllUsersQuery.toQuery(pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserGetDTO> getUserById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(mediator.send(GetUserByIdQuery.toQuery(String.valueOf(id))));
    }

    @GetMapping(params = "ids")
    public ResponseEntity<List<UserGetDTO>> getUsersByIds(@RequestParam List<String> ids) {
        return ResponseEntity.ok(mediator.send(GetUsersByIdsQuery.toQuery(ids)));
    }

    @GetMapping(params = "email")
    public ResponseEntity<UserGetDTO> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(mediator.send(GetUserByEmailQuery.toQuery(email)));
    }

    @PostMapping
    public ResponseEntity<UserGetDTO> create(@RequestBody @Valid UserCreateDTO userCreateDTO) {
        CreateUserCommand createUserCommand = createDTOCreateCommandMapper.toDto(userCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.send(createUserCommand));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserGetDTO> update(@RequestBody @Valid UserUpdateDTO userUpdateDTO, @PathVariable("id") UUID id) {
        UpdateUserCommand updateUserCommand = UpdateUserCommand.fromDTO(id, userUpdateDTO);
        return ResponseEntity.ok(mediator.send(updateUserCommand));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        mediator.send(DeleteUserCommand.toCommand(id));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
