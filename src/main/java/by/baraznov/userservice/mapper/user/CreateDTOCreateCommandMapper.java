package by.baraznov.userservice.mapper.user;


import by.baraznov.userservice.dto.user.UserCreateDTO;
import by.baraznov.userservice.mapper.BaseMapper;
import by.baraznov.userservice.write.command.CreateUserCommand;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface CreateDTOCreateCommandMapper extends BaseMapper<UserCreateDTO, CreateUserCommand> {
}
