package by.baraznov.userservice.repositories;

import by.baraznov.userservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByEmail(String email);

    @Query(value = "SELECT * FROM users AS u WHERE u.id IN :ids", nativeQuery = true)
    List<User> findUsersByIds(@Param(value = "ids")List<Integer> ids);
}
