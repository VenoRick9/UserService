package by.baraznov.userservice.mappers.card;

import by.baraznov.userservice.dtos.card.CardGetDTO;
import by.baraznov.userservice.mappers.BaseMapper;
import by.baraznov.userservice.models.CardInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapper.class)
public interface CardGetDTOMapper  extends BaseMapper<CardInfo, CardGetDTO>{
    @Mapping(source = "user.id", target = "userId")
    CardGetDTO toDto(CardInfo cardInfo);
}
