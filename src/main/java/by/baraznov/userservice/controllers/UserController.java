package by.baraznov.userservice.controllers;

import by.baraznov.userservice.dtos.user.UserCreateDTO;
import by.baraznov.userservice.dtos.user.UserGetDTO;
import by.baraznov.userservice.dtos.user.UserUpdateDTO;
import by.baraznov.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
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

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserGetDTO>> getAllUsers(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserGetDTO> getUserById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping(params = "ids")
    public ResponseEntity<List<UserGetDTO>> getUsersByIds(@RequestParam List<Integer> ids) {
        return ResponseEntity.ok(userService.getUsersByIds(ids));
    }

    @GetMapping(params = "email")
    public ResponseEntity<UserGetDTO> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PostMapping
    public ResponseEntity<UserGetDTO> create(@RequestBody @Valid UserCreateDTO userCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userCreateDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserGetDTO> update(@RequestBody @Valid UserUpdateDTO userUpdateDTO, @PathVariable("id") Integer id) {
        return ResponseEntity.ok(userService.update(userUpdateDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
