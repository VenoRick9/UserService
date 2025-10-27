package by.baraznov.userservice.write.handler;

import by.baraznov.userservice.dto.user.UserGetDTO;
import by.baraznov.userservice.mapper.user.UserCreateCommandMapper;
import by.baraznov.userservice.mapper.user.UserGetDTOMapper;
import by.baraznov.userservice.mediator.CommandHandler;
import by.baraznov.userservice.model.User;
import by.baraznov.userservice.repository.UserRepository;
import by.baraznov.userservice.util.EmailAlreadyExist;
import by.baraznov.userservice.write.command.CreateUserCommand;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class CreateUserHandler implements CommandHandler<CreateUserCommand, UserGetDTO> {
    private final UserRepository userRepository;
    private final UserCreateCommandMapper userCreateCommandMapper;
    private final UserGetDTOMapper userGetDTOMapper;

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
    public UserGetDTO handle(CreateUserCommand command) {
        User user = userCreateCommandMapper.toEntity(command);
        if (userRepository.findUserByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExist("User with email " + user.getEmail() + " already exists");
        }
        userRepository.save(user);
        return userGetDTOMapper.toDto(user);
    }
}
