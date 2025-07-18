package by.baraznov.userservice.mappers.user;

import by.baraznov.userservice.dtos.card.CardGetDTO;
import by.baraznov.userservice.dtos.user.UserGetDTO;
import by.baraznov.userservice.mappers.BaseMapper;
import by.baraznov.userservice.models.CardInfo;
import by.baraznov.userservice.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.util.List;

@Mapper(config = BaseMapper.class)
public interface UserGetDTOMapper extends BaseMapper<User, UserGetDTO> {

    @Mapping(target = "cards", source = "cards", qualifiedByName = "cardInfoToCardGetDTOList")
    UserGetDTO toDto(User user);
    @Named("cardInfoToCardGetDTO")
    default CardGetDTO cardToCardGetDTO(CardInfo cardInfo) {
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
        Integer userId = cardInfo.getUser().getId();
        return new CardGetDTO( id, userId, number, holder, expirationDate );
    }
    @Named("cardInfoToCardGetDTOList")
    default List<CardGetDTO> cardToGetDTOList(List<CardInfo> cards) {
        if (cards == null) return List.of();
        return cards.stream()
                .map(this::cardToCardGetDTO)
                .toList();
    }
}
