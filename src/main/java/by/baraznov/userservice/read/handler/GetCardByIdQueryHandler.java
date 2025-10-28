package by.baraznov.userservice.read.handler;

import by.baraznov.userservice.dto.card.CardGetDTO;
import by.baraznov.userservice.mediator.QueryHandler;
import by.baraznov.userservice.read.model.CardInfoQuery;
import by.baraznov.userservice.read.model.UserQuery;
import by.baraznov.userservice.read.query.GetCardByIdQuery;
import by.baraznov.userservice.read.repository.UserQueryRepository;
import by.baraznov.userservice.util.CardNotFound;
import by.baraznov.userservice.util.UserNotFound;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class GetCardByIdQueryHandler implements QueryHandler<GetCardByIdQuery, CardGetDTO> {
    private final UserQueryRepository userQueryRepository;


    @Override
    public CardGetDTO handle(GetCardByIdQuery query) {
        Integer cardId = query.id();

        UserQuery user = userQueryRepository.findByCards_Id(cardId)
                .orElseThrow(() -> new UserNotFound("User not found for card with id " + cardId));

        CardInfoQuery card = user.getCards().stream()
                .filter(c -> c.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new CardNotFound("Card with id " + cardId + " not found for user " + user.getId()));
        return CardGetDTO.builder()
                .id(card.getId())
                .number(card.getNumber())
                .holder(card.getHolder())
                .expirationDate(card.getExpirationDate())
                .userId(UUID.fromString(card.getUserId()))
                .build();
    }
}
