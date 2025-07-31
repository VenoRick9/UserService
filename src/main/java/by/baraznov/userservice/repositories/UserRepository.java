package by.baraznov.userservice.repositories;

import by.baraznov.userservice.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.cards WHERE u.email = :email")
    Optional<User> findUserByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.cards WHERE u.id IN :ids")
    List<User> findUsersByIds(@Param(value = "ids") List<Integer> ids);

    @Override
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.cards WHERE u.id = :id")
    Optional<User> findById(Integer id);

    @Override
    @Query(value = "SELECT u FROM User u LEFT JOIN FETCH u.cards",
            countQuery = "SELECT COUNT(u) FROM User u")
    Page<User> findAll(Pageable pageable);
}
