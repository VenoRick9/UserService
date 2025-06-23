package by.baraznov.userservice.mappers.card;

import by.baraznov.userservice.dtos.card.CardCreateDTO;
import by.baraznov.userservice.mappers.BaseMapper;
import by.baraznov.userservice.models.CardInfo;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface CardCreateDTOMapper extends BaseMapper<CardInfo, CardCreateDTO> {
}
