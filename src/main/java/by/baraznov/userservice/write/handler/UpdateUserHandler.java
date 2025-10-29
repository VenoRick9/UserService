package by.baraznov.userservice.write.handler;

import by.baraznov.userservice.dto.user.UserGetDTO;
import by.baraznov.userservice.dto.user.UserUpdatedEvent;
import by.baraznov.userservice.mapper.user.UserGetDTOMapper;
import by.baraznov.userservice.mapper.user.UserQueryToCommandMapper;
import by.baraznov.userservice.mapper.user.UserUpdateCommandMapper;
import by.baraznov.userservice.mediator.CommandHandler;
import by.baraznov.userservice.model.OutboxEvent;
import by.baraznov.userservice.read.model.UserQuery;
import by.baraznov.userservice.read.repository.UserQueryRepository;
import by.baraznov.userservice.repository.OutboxRepository;
import by.baraznov.userservice.util.UserNotFound;
import by.baraznov.userservice.write.command.UpdateUserCommand;
import by.baraznov.userservice.write.model.UserCommand;
import by.baraznov.userservice.write.repository.UserCommandRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@AllArgsConstructor
public class UpdateUserHandler implements CommandHandler<UpdateUserCommand, UserGetDTO> {
    private final UserCommandRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final OutboxRepository outboxRepository;
    private final UserUpdateCommandMapper userUpdateCommandMapper;
    private final UserQueryToCommandMapper userQueryToCommandMapper;
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
        UserCommand userUpdateEntity = userUpdateCommandMapper.toEntity(command);
        if (userUpdateEntity.getId() == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        UserQuery userQuery = userQueryRepository.findById(String.valueOf(userUpdateEntity.getId()))
                .orElseThrow(() -> new UserNotFound("User " + userUpdateEntity.getId() + " doesn't exist"));
        UserCommand user = userQueryToCommandMapper.toDto(userQuery);
        userUpdateCommandMapper.merge(user, command);
        user =  userRepository.save(user);
        UserUpdatedEvent event = UserUpdatedEvent.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .build();
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .aggregateId(user.getId())
                .aggregateType("User")
                .eventType("USER_UPDATED")
                .payload(event)
                .createdAt(LocalDateTime.now())
                .build();
        outboxRepository.save(outboxEvent);
        return userGetDTOMapper.toDto(user);
    }
}
