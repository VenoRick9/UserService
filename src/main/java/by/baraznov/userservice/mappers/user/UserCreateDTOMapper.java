package by.baraznov.userservice.mappers.user;


import by.baraznov.userservice.dtos.user.UserCreateDTO;
import by.baraznov.userservice.mappers.BaseMapper;
import by.baraznov.userservice.models.User;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface UserCreateDTOMapper extends BaseMapper<User, UserCreateDTO> {
}
