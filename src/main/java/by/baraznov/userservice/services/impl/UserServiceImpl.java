package by.baraznov.userservice.services.impl;

import by.baraznov.userservice.dtos.user.UserCreateDTO;
import by.baraznov.userservice.dtos.user.UserGetDTO;
import by.baraznov.userservice.dtos.user.UserUpdateDTO;
import by.baraznov.userservice.mappers.user.UserCreateDTOMapper;
import by.baraznov.userservice.mappers.user.UserGetDTOMapper;
import by.baraznov.userservice.mappers.user.UserUpdateDTOMapper;
import by.baraznov.userservice.models.User;
import by.baraznov.userservice.repositories.UserRepository;
import by.baraznov.userservice.services.UserService;
import by.baraznov.userservice.utils.EmailAlreadyExist;
import by.baraznov.userservice.utils.UserAlreadyExist;
import by.baraznov.userservice.utils.UserNotFound;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserGetDTOMapper userGetDTOMapper;
    private final UserUpdateDTOMapper userUpdateDTOMapper;
    private final UserCreateDTOMapper userCreateDTOMapper;

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "allUsers", allEntries = true)
            },
            put = {
                    @CachePut(cacheNames = "user", key = "#result.id()")
            }
    )
    public UserGetDTO create(UserCreateDTO userCreateDTO, Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();
        User user = userCreateDTOMapper.toEntity(userCreateDTO);
        if (userRepository.findUserByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExist("User with email " + user.getEmail() + " already exists");
        }
        if(userRepository.findById(userId).isPresent()) {
            throw new UserAlreadyExist("User with id " + userId + " already exists");
        }
        user.setId(userId);
        userRepository.save(user);
        return userGetDTOMapper.toDto(user);
    }

    @Override
    @Cacheable(value = "user", key = "#id")
    public UserGetDTO getUserById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return userGetDTOMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFound("User with id " + id + " doesn't exist")));
    }

    @Override
    public List<UserGetDTO> getUsersByIds(List<Integer> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("List with ids cannot be null");
        }
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return userGetDTOMapper.toDtos(userRepository.findUsersByIds(ids));
    }

    @Override
    @Cacheable(value = "allUsers", key = "#pageable")
    public Page<UserGetDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userGetDTOMapper::toDto);
    }

    @Override
    @Cacheable(value = "user", key = "#email")
    public UserGetDTO getUserByEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        return userGetDTOMapper.toDto(userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFound("User with email " + email + " doesn't exist")));
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "allUsers", allEntries = true)
            },
            put = {
                    @CachePut(cacheNames = "user", key = "#id")
            }
    )
    public UserGetDTO update(UserUpdateDTO userUpdateDTO, Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFound("User " + id + " doesn't exist"));
        userUpdateDTOMapper.merge(user, userUpdateDTO);
        userRepository.save(user);
        return userGetDTOMapper.toDto(user);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "user", key = "#id"),
                    @CacheEvict(cacheNames = "allUsers", allEntries = true)
            }
    )
    public void delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (!userRepository.existsById(id)) {
            throw new UserNotFound("User with id " + id + " doesn't exist");
        }
        userRepository.deleteById(id);
    }
}
