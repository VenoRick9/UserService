package by.baraznov.userservice.write.handler;

import by.baraznov.userservice.dto.user.UserCreatedEvent;
import by.baraznov.userservice.dto.user.UserGetDTO;
import by.baraznov.userservice.mapper.user.UserCreateCommandMapper;
import by.baraznov.userservice.mapper.user.UserGetDTOMapper;
import by.baraznov.userservice.mediator.CommandHandler;
import by.baraznov.userservice.model.OutboxEvent;
import by.baraznov.userservice.read.repository.UserQueryRepository;
import by.baraznov.userservice.repository.OutboxRepository;
import by.baraznov.userservice.util.EmailAlreadyExist;
import by.baraznov.userservice.util.UserAlreadyExist;
import by.baraznov.userservice.write.command.CreateUserCommand;
import by.baraznov.userservice.write.model.UserCommand;
import by.baraznov.userservice.write.repository.UserCommandRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;




@Component
@AllArgsConstructor
public class CreateUserHandler implements CommandHandler<CreateUserCommand, UserGetDTO> {
    private final UserCommandRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final OutboxRepository outboxRepository;
    private final UserCreateCommandMapper userCreateCommandMapper;
    private final UserGetDTOMapper userGetDTOMapper;


    @Override
    @Transactional
//    @Caching(
//            evict = {
//                    @CacheEvict(cacheNames = "allUsers", allEntries = true)
//            },
//            put = {
//                    @CachePut(cacheNames = "user", key = "T(String).valueOf(#result.id())")
//            }
//    )
    public UserGetDTO handle(CreateUserCommand command) {
        UserCommand user = userCreateCommandMapper.toEntity(command);
        if (userQueryRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExist("User with email " + user.getEmail() + " already exists");
        }
        if(userQueryRepository.findById(String.valueOf(user.getId())).isPresent()) {
            throw new UserAlreadyExist("User with id " + user.getId() + " already exists");
        }
        userRepository.save(user);
        UserCreatedEvent event = UserCreatedEvent.builder()
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
                .eventType("USER_CREATED")
                .payload(event)
                .createdAt(LocalDateTime.now())
                .build();
        outboxRepository.save(outboxEvent);
        return userGetDTOMapper.toDto(user);
    }
}
