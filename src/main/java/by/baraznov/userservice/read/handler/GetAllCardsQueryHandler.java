package by.baraznov.userservice.read.handler;

import by.baraznov.userservice.dto.card.CardGetDTO;
import by.baraznov.userservice.mediator.QueryHandler;
import by.baraznov.userservice.read.model.UserQuery;
import by.baraznov.userservice.read.query.GetAllCardsQuery;
import by.baraznov.userservice.read.repository.UserQueryRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class GetAllCardsQueryHandler implements QueryHandler<GetAllCardsQuery, Page<CardGetDTO>> {
    private final UserQueryRepository userQueryRepository;


    @Override
    public Page<CardGetDTO> handle(GetAllCardsQuery query) {
        Pageable pageable = query.pageable();

        List<UserQuery> users = userQueryRepository.findAll();

        List<CardGetDTO> allCards = users.stream()
                .flatMap(user -> user.getCards().stream())
                .map(card -> CardGetDTO.builder()
                        .id(card.getId())
                        .number(card.getNumber())
                        .holder(card.getHolder())
                        .expirationDate(card.getExpirationDate())
                        .userId(UUID.fromString(card.getUserId()))
                        .build()
                )
                .collect(Collectors.toList());

        int start = Math.min((int) pageable.getOffset(), allCards.size());
        int end = Math.min(start + pageable.getPageSize(), allCards.size());

        List<CardGetDTO> pageContent = allCards.subList(start, end);

        return new PageImpl<>(pageContent, pageable, allCards.size());
    }

}
