package by.baraznov.userservice.mapper.card;

import by.baraznov.userservice.mapper.BaseMapper;
import by.baraznov.userservice.write.command.CreateCardCommand;
import by.baraznov.userservice.write.model.CardInfoCommand;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface CardCreateCommandMapper extends BaseMapper<CardInfoCommand, CreateCardCommand> {
}
