package by.baraznov.userservice.write.handler;

import by.baraznov.userservice.dto.card.CardDeletedEvent;
import by.baraznov.userservice.mediator.CommandHandler;
import by.baraznov.userservice.model.OutboxEvent;
import by.baraznov.userservice.read.repository.UserQueryRepository;
import by.baraznov.userservice.repository.OutboxRepository;
import by.baraznov.userservice.util.UserNotFound;
import by.baraznov.userservice.write.command.DeleteCardCommand;
import by.baraznov.userservice.write.repository.CardInfoCommandRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@AllArgsConstructor
public class DeleteCardHandler implements CommandHandler<DeleteCardCommand, Void> {
    private final CardInfoCommandRepository cardInfoCommandRepository;
    private final UserQueryRepository userQueryRepository;
    private final OutboxRepository outboxRepository;

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "user", key = "#command.id()"),
                    @CacheEvict(cacheNames = "allUsers", allEntries = true)
            }
    )
    public Void handle(DeleteCardCommand command) {
        if (command.id() == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (!cardInfoCommandRepository.existsById(command.id())) {
            throw new UserNotFound("Card with id " + command.id() + " doesn't exist");
        }
        UUID userId = UUID.randomUUID();
        CardDeletedEvent event = CardDeletedEvent.builder()
                .id(userId)
                .cardId(command.id())
                .build();
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .aggregateId(userId)
                .aggregateType("CARD")
                .eventType("CARD_DELETED")
                .payload(event)
                .createdAt(LocalDateTime.now())
                .build();
        outboxRepository.save(outboxEvent);
        cardInfoCommandRepository.deleteById(command.id());
        return null;
    }
}
