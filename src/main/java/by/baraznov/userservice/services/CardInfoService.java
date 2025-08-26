package by.baraznov.userservice.services;

import by.baraznov.userservice.dtos.card.CardCreateDTO;
import by.baraznov.userservice.dtos.card.CardGetDTO;
import by.baraznov.userservice.dtos.card.CardUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CardInfoService {
    CardGetDTO create(CardCreateDTO cardCreateDTO, String authentication);

    CardGetDTO getCardById(Integer id);

    List<CardGetDTO> getCardsByIds(List<Integer> ids);

    Page<CardGetDTO> getAllCards(Pageable pageable);

    CardGetDTO update(CardUpdateDTO cardUpdateDTO, Integer id);

    void delete(Integer id);
}

