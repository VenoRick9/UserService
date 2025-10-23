package by.baraznov.userservice.mapper.card;

import by.baraznov.userservice.dto.card.CardUpdateDTO;
import by.baraznov.userservice.mapper.BaseMapper;
import by.baraznov.userservice.model.CardInfo;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface CardUpdateDTOMapper extends BaseMapper<CardInfo, CardUpdateDTO> {
}
