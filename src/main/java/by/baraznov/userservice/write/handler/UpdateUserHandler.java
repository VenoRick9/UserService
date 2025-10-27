package by.baraznov.userservice.write.handler;

import by.baraznov.userservice.dto.user.UserGetDTO;
import by.baraznov.userservice.mapper.user.UserGetDTOMapper;
import by.baraznov.userservice.mapper.user.UserUpdateCommandMapper;
import by.baraznov.userservice.mapper.user.UserUpdateDTOMapper;
import by.baraznov.userservice.mediator.CommandHandler;
import by.baraznov.userservice.model.User;
import by.baraznov.userservice.repository.UserRepository;
import by.baraznov.userservice.util.UserNotFound;
import by.baraznov.userservice.write.command.UpdateUserCommand;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class UpdateUserHandler implements CommandHandler<UpdateUserCommand, UserGetDTO> {
    private final UserRepository userRepository;
    private final UserUpdateCommandMapper userUpdateCommandMapper;
    private final UserUpdateDTOMapper userUpdateDTOMapper;
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
    public UserGetDTO handle(UpdateUserCommand command) {
        User userUpdateEntity = userUpdateCommandMapper.toEntity(command);
        if (userUpdateEntity.getId() == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        User user = userRepository.findById(userUpdateEntity.getId())
                .orElseThrow(() -> new UserNotFound("User " + userUpdateEntity.getId() + " doesn't exist"));
        userUpdateCommandMapper.merge(user, command);
        return userGetDTOMapper.toDto( userRepository.save(user));
    }
}
