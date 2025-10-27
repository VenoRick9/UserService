package by.baraznov.userservice.mapper.user;


import by.baraznov.userservice.mapper.BaseMapper;
import by.baraznov.userservice.write.command.UpdateUserCommand;
import by.baraznov.userservice.write.model.UserCommand;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface UserUpdateCommandMapper extends BaseMapper<UserCommand, UpdateUserCommand> {
}
