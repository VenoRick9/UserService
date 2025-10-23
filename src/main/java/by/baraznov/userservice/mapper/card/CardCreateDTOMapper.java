package by.baraznov.userservice.mapper.card;

import by.baraznov.userservice.dto.card.CardCreateDTO;
import by.baraznov.userservice.mapper.BaseMapper;
import by.baraznov.userservice.model.CardInfo;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface CardCreateDTOMapper extends BaseMapper<CardInfo, CardCreateDTO> {
}
