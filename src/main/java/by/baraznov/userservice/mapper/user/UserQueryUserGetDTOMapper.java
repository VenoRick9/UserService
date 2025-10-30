package by.baraznov.userservice.mapper.user;


import by.baraznov.userservice.dto.user.UserGetDTO;
import by.baraznov.userservice.mapper.BaseMapper;
import by.baraznov.userservice.read.model.UserQuery;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface UserQueryUserGetDTOMapper extends BaseMapper<UserQuery, UserGetDTO> {
}
