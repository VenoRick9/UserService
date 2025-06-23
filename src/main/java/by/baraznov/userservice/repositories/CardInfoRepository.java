package by.baraznov.userservice.repositories;

import by.baraznov.userservice.models.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Integer> {
    @Query(value = "SELECT c FROM CardInfo c WHERE c.id IN (:ids)")
    List<CardInfo> findCardsByIds(@Param(value = "ids")List<Integer> ids);

    boolean existsByNumber(String number);
}
