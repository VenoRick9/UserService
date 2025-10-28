package by.baraznov.userservice.read.repository;

import by.baraznov.userservice.read.model.UserQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserQueryRepository extends MongoRepository<UserQuery, String> {
    Optional<UserQuery> findByEmail(String email);
    List<UserQuery> findByIdIn(List<String> ids);
    Optional<UserQuery> findByCardsNumber(String number);
    Optional<UserQuery> findByCards_Id(Integer cardId);

}
