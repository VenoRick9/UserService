package by.baraznov.userservice.mapper.user;


import by.baraznov.userservice.dto.user.UserCreateDTO;
import by.baraznov.userservice.mapper.BaseMapper;
import by.baraznov.userservice.model.User;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface UserCreateDTOMapper extends BaseMapper<User, UserCreateDTO> {
}
