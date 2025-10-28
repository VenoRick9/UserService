package by.baraznov.userservice.read.handler;

import by.baraznov.userservice.dto.card.CardGetDTO;
import by.baraznov.userservice.mediator.QueryHandler;
import by.baraznov.userservice.read.model.UserQuery;
import by.baraznov.userservice.read.query.GetCardsByIdsQuery;
import by.baraznov.userservice.read.repository.UserQueryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class GetCardsByIdsQueryHandler implements QueryHandler<GetCardsByIdsQuery, List<CardGetDTO>> {
    private final UserQueryRepository userQueryRepository;


    @Override
    public List<CardGetDTO> handle(GetCardsByIdsQuery query) {
        List<Integer> ids = query.ids();

        List<UserQuery> users = userQueryRepository.findByCardsIdIn(ids);

        List<CardGetDTO> result = new ArrayList<>();
        for (UserQuery user : users) {
            user.getCards().stream()
                    .filter(card -> ids.contains(card.getId()))
                    .forEach(card -> result.add(
                            CardGetDTO.builder()
                                    .id(card.getId())
                                    .number(card.getNumber())
                                    .holder(card.getHolder())
                                    .expirationDate(card.getExpirationDate())
                                    .userId(UUID.fromString(card.getUserId()))
                                    .build()
                    ));
        }

        return result;


    }
}
