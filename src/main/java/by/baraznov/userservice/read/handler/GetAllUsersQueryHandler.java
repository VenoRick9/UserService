package by.baraznov.userservice.read.handler;

import by.baraznov.userservice.dto.user.UserGetDTO;
import by.baraznov.userservice.mapper.user.UserQueryUserGetDTOMapper;
import by.baraznov.userservice.mediator.QueryHandler;
import by.baraznov.userservice.read.query.GetAllUsersQuery;
import by.baraznov.userservice.read.repository.UserQueryRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetAllUsersQueryHandler implements QueryHandler<GetAllUsersQuery, Page<UserGetDTO>> {
    private final UserQueryRepository userQueryRepository;
    private final UserQueryUserGetDTOMapper userQueryUserGetDTOMapper;

    @Override
    //@Cacheable(value = "allUsers", key = "#query.pageable()")
    public Page<UserGetDTO> handle(GetAllUsersQuery query) {
        return userQueryRepository.findAll(query.pageable()).map(userQueryUserGetDTOMapper::toDto);
    }
}
