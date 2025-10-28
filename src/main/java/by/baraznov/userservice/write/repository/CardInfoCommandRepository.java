package by.baraznov.userservice.write.repository;

import by.baraznov.userservice.write.model.CardInfoCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardInfoCommandRepository extends JpaRepository<CardInfoCommand, Integer> {
 /*   @Query(value = "SELECT c FROM CardInfo c LEFT JOIN FETCH c.user WHERE c.id IN (:ids)")
    List<CardInfo> findCardsByIds(@Param(value = "ids") List<Integer> ids);

    @Override
    @Query(value = "SELECT c FROM CardInfo c LEFT JOIN FETCH c.user",
            countQuery = "SELECT COUNT (c) FROM CardInfo c")
    Page<CardInfo> findAll(Pageable pageable);

    @Override
    @Query("SELECT c FROM CardInfo c LEFT JOIN FETCH c.user WHERE c.id = :id")
    Optional<CardInfo> findById(Integer id);

    boolean existsByNumber(String number);*/
}
