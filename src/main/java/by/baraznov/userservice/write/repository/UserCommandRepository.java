package by.baraznov.userservice.write.repository;

import by.baraznov.userservice.write.model.UserCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserCommandRepository extends JpaRepository<UserCommand, UUID> {

}
