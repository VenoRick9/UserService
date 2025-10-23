package by.baraznov.userservice.mapper.card;

import by.baraznov.userservice.dto.card.CardGetDTO;
import by.baraznov.userservice.mapper.BaseMapper;
import by.baraznov.userservice.model.CardInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapper.class)
public interface CardGetDTOMapper  extends BaseMapper<CardInfo, CardGetDTO>{
    @Mapping(source = "user.id", target = "userId")
    CardGetDTO toDto(CardInfo cardInfo);
}
