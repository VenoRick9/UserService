package by.baraznov.userservice.service.impl;

import by.baraznov.userservice.dto.user.UserCreateDTO;
import by.baraznov.userservice.dto.user.UserGetDTO;
import by.baraznov.userservice.dto.user.UserUpdateDTO;
import by.baraznov.userservice.mapper.user.UserCreateDTOMapper;
import by.baraznov.userservice.mapper.user.UserGetDTOMapper;
import by.baraznov.userservice.mapper.user.UserUpdateDTOMapper;
import by.baraznov.userservice.service.UserService;
import by.baraznov.userservice.util.JwtUtils;
import by.baraznov.userservice.util.UserNotFound;
import by.baraznov.userservice.write.model.UserCommand;
import by.baraznov.userservice.write.repository.UserCommandRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserCommandRepository userRepository;
    private final UserGetDTOMapper userGetDTOMapper;
    private final UserUpdateDTOMapper userUpdateDTOMapper;
    private final UserCreateDTOMapper userCreateDTOMapper;
    private final JwtUtils jwtUtils;

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
    public UserGetDTO create(UserCreateDTO userCreateDTO) {
        UserCommand user = userCreateDTOMapper.toEntity(userCreateDTO);
        /*if (userRepository.findUserByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExist("User with email " + user.getEmail() + " already exists");
        }*/
        userRepository.save(user);
        return userGetDTOMapper.toDto(user);
    }

    @Override
    @Cacheable(value = "user", key = "#id")
    public UserGetDTO getUserById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return userGetDTOMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFound("User with id " + id + " doesn't exist")));
    }

    @Override
    public List<UserGetDTO> getUsersByIds(List<UUID> ids) {
       /* if (ids == null) {
            throw new IllegalArgumentException("List with ids cannot be null");
        }
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return userGetDTOMapper.toDtos(userRepository.findUsersByIds(ids));*/
        return null;
    }

    @Override
    @Cacheable(value = "allUsers", key = "#pageable")
    public Page<UserGetDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userGetDTOMapper::toDto);
    }

    @Override
    @Cacheable(value = "user", key = "#email")
    public UserGetDTO getUserByEmail(String email) {
        /*if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        return userGetDTOMapper.toDto(userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFound("User with email " + email + " doesn't exist")));*/
        return null;
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
    public UserGetDTO update(UserUpdateDTO userUpdateDTO, UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        UserCommand user = userRepository.findById(id)
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
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (!userRepository.existsById(id)) {
            throw new UserNotFound("User with id " + id + " doesn't exist");
        }
        userRepository.deleteById(id);
    }
}
