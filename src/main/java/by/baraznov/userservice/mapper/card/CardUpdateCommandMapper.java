package by.baraznov.userservice.mapper.card;


import by.baraznov.userservice.mapper.BaseMapper;
import by.baraznov.userservice.read.model.CardInfoQuery;
import by.baraznov.userservice.write.command.UpdateCardCommand;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface CardUpdateCommandMapper extends BaseMapper<CardInfoQuery, UpdateCardCommand> {
}
