package by.baraznov.userservice.mapper.user;


import by.baraznov.userservice.mapper.BaseMapper;
import by.baraznov.userservice.read.model.UserQuery;
import by.baraznov.userservice.write.model.UserCommand;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface UserQueryToCommandMapper extends BaseMapper<UserQuery, UserCommand> {
}
