package by.baraznov.userservice.write.handler;

import by.baraznov.userservice.dto.card.CardCreatedEvent;
import by.baraznov.userservice.dto.card.CardGetDTO;
import by.baraznov.userservice.mapper.card.CardCreateCommandMapper;
import by.baraznov.userservice.mapper.card.CardGetDTOMapper;
import by.baraznov.userservice.mapper.user.UserQueryToCommandMapper;
import by.baraznov.userservice.mediator.CommandHandler;
import by.baraznov.userservice.model.OutboxEvent;
import by.baraznov.userservice.read.repository.UserQueryRepository;
import by.baraznov.userservice.repository.OutboxRepository;
import by.baraznov.userservice.util.CardAlreadyExist;
import by.baraznov.userservice.util.JwtUtils;
import by.baraznov.userservice.util.UserNotFound;
import by.baraznov.userservice.write.command.CreateCardCommand;
import by.baraznov.userservice.write.model.CardInfoCommand;
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
public class CreateCardHandler implements CommandHandler<CreateCardCommand, CardGetDTO> {
    private final JwtUtils jwtUtils;
    private final UserQueryRepository userQueryRepository;
    private final OutboxRepository outboxRepository;
    private final CardInfoCommandRepository cardInfoCommandRepository;
    private final CardCreateCommandMapper cardCreateCommandMapper;
    private final UserQueryToCommandMapper userQueryToCommandMapper;
    private final CardGetDTOMapper cardGetDTOMapper;



    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "user", key = "#result.userId()"),
                    @CacheEvict(cacheNames = "allUsers", allEntries = true)
            }
    )
    public CardGetDTO handle(CreateCardCommand command) {
        String token = command.token().startsWith("Bearer ") ?
                command.token().substring(7) : command.token();
        UUID userId = jwtUtils.getAccessClaims(token);
        CardInfoCommand cardInfo = cardCreateCommandMapper.toEntity(command);

        if(userQueryRepository.findByCardsNumber(cardInfo.getNumber()).isPresent()){
            throw new CardAlreadyExist("Card number " + cardInfo.getNumber() + " already exist");
        }
        cardInfo.setUser(userQueryToCommandMapper.toDto(userQueryRepository.findById(String.valueOf(userId))
                .orElseThrow(() -> new UserNotFound("User with id " + userId + " doesn't exist"))));
        cardInfo = cardInfoCommandRepository.save(cardInfo);
        CardCreatedEvent event = CardCreatedEvent.builder()
                .id(userId)
                .cardId(cardInfo.getId())
                .number(cardInfo.getNumber())
                .holder(cardInfo.getHolder())
                .expirationDate(cardInfo.getExpirationDate())
                .build();
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .aggregateId(userId)
                .aggregateType("Card")
                .eventType("CARD_CREATED")
                .payload(event)
                .createdAt(LocalDateTime.now())
                .build();
        outboxRepository.save(outboxEvent);
        return cardGetDTOMapper.toDto(cardInfo);







    }
}
