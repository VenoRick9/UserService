package by.baraznov.userservice.read.handler;

import by.baraznov.userservice.dto.user.UserGetDTO;
import by.baraznov.userservice.mapper.user.UserQueryUserGetDTOMapper;
import by.baraznov.userservice.mediator.QueryHandler;
import by.baraznov.userservice.read.query.GetUsersByIdsQuery;
import by.baraznov.userservice.read.repository.UserQueryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
public class GetUsersByIdsQueryHandler implements QueryHandler<GetUsersByIdsQuery, List<UserGetDTO>> {
    private final UserQueryRepository userQueryRepository;
    private final UserQueryUserGetDTOMapper userQueryUserGetDTOMapper;

    @Override
    public List<UserGetDTO> handle(GetUsersByIdsQuery query) {
        if (query.ids() == null) {
            throw new IllegalArgumentException("List with ids cannot be null");
        }
        if (query.ids().isEmpty()) {
            return Collections.emptyList();
        }
        return userQueryUserGetDTOMapper.toDtos(userQueryRepository.findByIdIn(query.ids()));

    }
}
