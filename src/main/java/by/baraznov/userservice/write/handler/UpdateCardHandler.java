package by.baraznov.userservice.write.handler;

import by.baraznov.userservice.dto.card.CardGetDTO;
import by.baraznov.userservice.dto.card.CardUpdateEvent;
import by.baraznov.userservice.mapper.card.CardGetDTOMapper;
import by.baraznov.userservice.mapper.card.CardQueryToCommandMapper;
import by.baraznov.userservice.mapper.card.CardUpdateCommandMapper;
import by.baraznov.userservice.mapper.user.UserQueryToCommandMapper;
import by.baraznov.userservice.mediator.CommandHandler;
import by.baraznov.userservice.model.OutboxEvent;
import by.baraznov.userservice.read.model.CardInfoQuery;
import by.baraznov.userservice.read.model.UserQuery;
import by.baraznov.userservice.read.repository.UserQueryRepository;
import by.baraznov.userservice.repository.OutboxRepository;
import by.baraznov.userservice.util.CardAlreadyExist;
import by.baraznov.userservice.util.CardNotFound;
import by.baraznov.userservice.util.UserNotFound;
import by.baraznov.userservice.write.command.UpdateCardCommand;
import by.baraznov.userservice.write.model.CardInfoCommand;
import by.baraznov.userservice.write.repository.CardInfoCommandRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


@Component
@AllArgsConstructor
public class UpdateCardHandler implements CommandHandler<UpdateCardCommand, CardGetDTO> {

    private final UserQueryRepository userQueryRepository;
    private final CardInfoCommandRepository cardInfoRepository;
    private final OutboxRepository outboxRepository;
    private final UserQueryToCommandMapper userQueryToCommandMapper;
    private final CardGetDTOMapper cardGetDTOMapper;
    private final CardUpdateCommandMapper cardUpdateCommandMapper;
    private final CardQueryToCommandMapper cardQueryToCommandMapper;


    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "user", key = "#result.userId()"),
                    @CacheEvict(cacheNames = "allUsers", allEntries = true)
            }
    )
    public CardGetDTO handle(UpdateCardCommand command) {
        if (command.id() == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        UserQuery userQuery = userQueryRepository.findByCards_Id(command.id())
                .orElseThrow(() -> new CardNotFound("Card with id " + command.id() + " doesn't exist"));
        CardInfoQuery cardInfoQuery = findCardById(userQuery, command.id());

        if(command.number() != null && userQueryRepository.findByCardsNumber(command.number()).isPresent()){
            throw new CardAlreadyExist("Card number " + command.number() + " already exist");
        }

        cardInfoQuery = cardUpdateCommandMapper.merge(cardInfoQuery, command);
        CardInfoCommand cardCommand = cardQueryToCommandMapper.toCommand(cardInfoQuery);
        cardCommand.setUser(userQueryToCommandMapper.toDto(userQueryRepository.findById(String.valueOf(userQuery.getId()))
                .orElseThrow(() -> new UserNotFound("User with id " + userQuery.getId() + " doesn't exist"))));

        cardCommand = cardInfoRepository.save(cardCommand);

        CardUpdateEvent event = CardUpdateEvent.builder()
                .id(UUID.fromString(userQuery.getId()))
                .cardId(command.id())
                .number(command.number())
                .holder(command.holder())
                .expirationDate(command.expirationDate())
                .build();
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .aggregateId(UUID.fromString(userQuery.getId()))
                .aggregateType("Card")
                .eventType("CARD_UPDATED")
                .payload(event)
                .createdAt(LocalDateTime.now())
                .build();
        outboxRepository.save(outboxEvent);
        return cardGetDTOMapper.toDto(cardCommand);
    }
    private CardInfoQuery findCardById(UserQuery user, Integer cardId) {
        return user.getCards().stream()
                .filter(card -> Objects.equals(card.getId(), cardId))
                .findFirst()
                .orElseThrow(() -> new CardNotFound("Card with id " + cardId + " doesn't exist"));
    }
}
