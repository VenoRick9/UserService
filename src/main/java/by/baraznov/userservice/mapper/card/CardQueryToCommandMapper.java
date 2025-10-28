package by.baraznov.userservice.mapper.card;

import by.baraznov.userservice.mapper.BaseMapper;
import by.baraznov.userservice.read.model.CardInfoQuery;
import by.baraznov.userservice.write.model.CardInfoCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapper.class)
public interface CardQueryToCommandMapper {

    @Mapping(target = "user", ignore = true)
    CardInfoCommand toCommand(CardInfoQuery query);
}