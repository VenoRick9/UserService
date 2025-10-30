package by.baraznov.userservice.write.handler;

import by.baraznov.userservice.dto.card.CardDeletedEvent;
import by.baraznov.userservice.mediator.CommandHandler;
import by.baraznov.userservice.model.OutboxEvent;
import by.baraznov.userservice.read.model.UserQuery;
import by.baraznov.userservice.read.repository.UserQueryRepository;
import by.baraznov.userservice.repository.OutboxRepository;
import by.baraznov.userservice.util.CardNotFound;
import by.baraznov.userservice.util.UserNotFound;
import by.baraznov.userservice.write.command.DeleteCardCommand;
import by.baraznov.userservice.write.repository.CardInfoCommandRepository;
import lombok.AllArgsConstructor;
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
    //@CacheEvict(cacheNames = "allUsers", allEntries = true)
    public Void handle(DeleteCardCommand command) {
        if (command.id() == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        UserQuery user = userQueryRepository.findByCards_Id(command.id())
                .orElseThrow(() -> new UserNotFound(
                        "User not found for card with id " + command.id()
                ));
        boolean removed = user.getCards().removeIf(card -> card.getId().equals(command.id()));
        if (!removed) {
            throw new CardNotFound("Card with id " + command.id() + " not found for user " + user.getId());
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
