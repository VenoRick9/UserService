package by.baraznov.userservice.service;

import by.baraznov.userservice.dto.user.UserCreateDTO;
import by.baraznov.userservice.dto.user.UserGetDTO;
import by.baraznov.userservice.dto.user.UserUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserGetDTO create(UserCreateDTO userCreateDTO);

    UserGetDTO getUserById(UUID id);

    List<UserGetDTO> getUsersByIds(List<UUID> ids);

    Page<UserGetDTO> getAllUsers(Pageable pageable);

    UserGetDTO getUserByEmail(String email);

    UserGetDTO update(UserUpdateDTO userUpdateDTO, UUID id);

    void delete(UUID id);
}
