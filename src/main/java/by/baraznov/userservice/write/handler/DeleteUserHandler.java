package by.baraznov.userservice.write.handler;

import by.baraznov.userservice.dto.user.UserDeletedEvent;
import by.baraznov.userservice.mediator.CommandHandler;
import by.baraznov.userservice.model.OutboxEvent;
import by.baraznov.userservice.repository.OutboxRepository;
import by.baraznov.userservice.util.UserNotFound;
import by.baraznov.userservice.write.command.DeleteUserCommand;
import by.baraznov.userservice.write.repository.UserCommandRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@AllArgsConstructor
public class DeleteUserHandler implements CommandHandler<DeleteUserCommand, Void> {
    private final UserCommandRepository userRepository;
    private final OutboxRepository outboxRepository;

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "user", key = "#command.id()"),
                    @CacheEvict(cacheNames = "allUsers", allEntries = true)
            }
    )
    public Void handle(DeleteUserCommand command) {
        if (command.id() == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (!userRepository.existsById(command.id())) {
            throw new UserNotFound("User with id " + command.id() + " doesn't exist");
        }
        UserDeletedEvent event = UserDeletedEvent.builder()
                .id(command.id())
                .build();
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .aggregateId(command.id())
                .aggregateType("User")
                .eventType("USER_DELETED")
                .payload(event)
                .createdAt(LocalDateTime.now())
                .build();
        outboxRepository.save(outboxEvent);
        userRepository.deleteById(command.id());
        return null;
    }
}
