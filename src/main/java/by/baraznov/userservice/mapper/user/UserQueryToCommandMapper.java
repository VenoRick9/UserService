package by.baraznov.userservice.mapper.user;


import by.baraznov.userservice.mapper.BaseMapper;
import by.baraznov.userservice.read.model.CardInfoQuery;
import by.baraznov.userservice.read.model.UserQuery;
import by.baraznov.userservice.write.model.CardInfoCommand;
import by.baraznov.userservice.write.model.UserCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(config = BaseMapper.class, componentModel = "spring")
public interface UserQueryToCommandMapper extends BaseMapper<UserQuery, UserCommand> {

    default UserCommand toDto(UserQuery userQuery) {
        if (userQuery == null) return null;

        UserCommand user = new UserCommand();
        user.setId(UUID.fromString(userQuery.getId()));
        user.setName(userQuery.getName());
        user.setSurname(userQuery.getSurname());
        user.setEmail(userQuery.getEmail());
        user.setBirthDate(userQuery.getBirthDate());

        if (userQuery.getCards() != null) {
            List<CardInfoCommand> cards = userQuery.getCards().stream()
                    .map(cardQuery -> mapCard(cardQuery, user))
                    .toList();
            user.setCards(cards);
        } else {
            user.setCards(List.of());
        }

        return user;
    }

    @Named("mapCard")
    default CardInfoCommand mapCard(CardInfoQuery card, UserCommand user) {
        if (card == null) return null;
        CardInfoCommand command = new CardInfoCommand();
        command.setId(card.getId());
        command.setNumber(card.getNumber());
        command.setHolder(card.getHolder());
        command.setExpirationDate(card.getExpirationDate());
        command.setUser(user);
        return command;
    }
}
