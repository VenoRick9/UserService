package by.baraznov.userservice.services;

import by.baraznov.userservice.dtos.user.UserCreateDTO;
import by.baraznov.userservice.dtos.user.UserGetDTO;
import by.baraznov.userservice.dtos.user.UserUpdateDTO;

import java.util.List;

public interface UserService {
    UserGetDTO create(UserCreateDTO userCreateDTO);
    UserGetDTO getUserById(Integer id);
    List<UserGetDTO> getUsersByIds(List<Integer> ids);
    List<UserGetDTO> getAllUsers();
    UserGetDTO getUserByEmail(String email);
    UserGetDTO update(UserUpdateDTO userUpdateDTO, Integer id);
    void delete(Integer id);
}
