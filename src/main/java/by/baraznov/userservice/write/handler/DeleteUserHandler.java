package by.baraznov.userservice.write.handler;

import by.baraznov.userservice.mediator.CommandHandler;
import by.baraznov.userservice.repository.UserRepository;
import by.baraznov.userservice.util.UserNotFound;
import by.baraznov.userservice.write.command.DeleteUserCommand;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class DeleteUserHandler implements CommandHandler<DeleteUserCommand, Void> {
    private final UserRepository userRepository;

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
        userRepository.deleteById(command.id());
        return null;
    }
}
