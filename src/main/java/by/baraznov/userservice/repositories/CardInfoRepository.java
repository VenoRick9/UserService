package by.baraznov.userservice.repositories;

import by.baraznov.userservice.models.CardInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Integer> {
    @Query(value = "SELECT c FROM CardInfo c LEFT JOIN FETCH c.user WHERE c.id IN (:ids)")
    List<CardInfo> findCardsByIds(@Param(value = "ids") List<Integer> ids);

    @Override
    @Query(value = "SELECT c FROM CardInfo c LEFT JOIN FETCH c.user",
            countQuery = "SELECT COUNT (c) FROM CardInfo c")
    Page<CardInfo> findAll(Pageable pageable);

    @Override
    @Query("SELECT c FROM CardInfo c LEFT JOIN FETCH c.user WHERE c.id = :id")
    Optional<CardInfo> findById(Integer id);

    boolean existsByNumber(String number);
}
