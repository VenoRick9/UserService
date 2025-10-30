package by.baraznov.userservice.mapper.user;

import by.baraznov.userservice.dto.card.CardGetDTO;
import by.baraznov.userservice.dto.user.UserGetDTO;
import by.baraznov.userservice.mapper.BaseMapper;
import by.baraznov.userservice.write.model.CardInfoCommand;
import by.baraznov.userservice.write.model.UserCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Mapper(config = BaseMapper.class)
public interface UserGetDTOMapper extends BaseMapper<UserCommand, UserGetDTO> {

    @Mapping(target = "cards", source = "cards", qualifiedByName = "cardInfoToCardGetDTOList")
    UserGetDTO toDto(UserCommand user);
    @Named("cardInfoToCardGetDTO")
    default CardGetDTO cardToCardGetDTO(CardInfoCommand cardInfo) {
        if ( cardInfo == null ) {
            return null;
        }
        Integer id = null;
        String number = null;
        String holder = null;
        LocalDate expirationDate = null;
        id = cardInfo.getId();
        number = cardInfo.getNumber();
        holder = cardInfo.getHolder();
        expirationDate = cardInfo.getExpirationDate();
        UUID userId = cardInfo.getUser().getId();
        return new CardGetDTO( id, userId, number, holder, expirationDate );
    }
    @Named("cardInfoToCardGetDTOList")
    default List<CardGetDTO> cardToGetDTOList(List<CardInfoCommand> cards) {
        if (cards == null) return List.of();
        return cards.stream()
                .map(this::cardToCardGetDTO)
                .toList();
    }
}
