package by.baraznov.userservice.services;

import by.baraznov.userservice.dtos.card.CardCreateDTO;
import by.baraznov.userservice.dtos.card.CardGetDTO;
import by.baraznov.userservice.dtos.card.CardUpdateDTO;

import java.util.List;

public interface CardInfoService {
    CardGetDTO create(CardCreateDTO cardCreateDTO, Integer userId);
    CardGetDTO getCardById(Integer id);
    List<CardGetDTO> getCardsByIds(List<Integer> ids);
    List<CardGetDTO> getAllCards();
    CardGetDTO update(CardUpdateDTO cardUpdateDTO, Integer id);
    void delete(Integer id);
}

