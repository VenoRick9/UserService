package by.baraznov.userservice.services;

import by.baraznov.userservice.dtos.user.UserCreateDTO;
import by.baraznov.userservice.dtos.user.UserGetDTO;
import by.baraznov.userservice.dtos.user.UserUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserGetDTO create(UserCreateDTO userCreateDTO);

    UserGetDTO getUserById(Integer id);

    List<UserGetDTO> getUsersByIds(List<Integer> ids);

    Page<UserGetDTO> getAllUsers(Pageable pageable);

    UserGetDTO getUserByEmail(String email);

    UserGetDTO update(UserUpdateDTO userUpdateDTO, Integer id);

    void delete(Integer id);
}
