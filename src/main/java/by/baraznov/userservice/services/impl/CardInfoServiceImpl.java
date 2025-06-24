package by.baraznov.userservice.services.impl;

import by.baraznov.userservice.dtos.card.CardCreateDTO;
import by.baraznov.userservice.dtos.card.CardGetDTO;
import by.baraznov.userservice.dtos.card.CardUpdateDTO;
import by.baraznov.userservice.mappers.card.CardCreateDTOMapper;
import by.baraznov.userservice.mappers.card.CardGetDTOMapper;
import by.baraznov.userservice.mappers.card.CardUpdateDTOMapper;
import by.baraznov.userservice.models.CardInfo;
import by.baraznov.userservice.repositories.CardInfoRepository;
import by.baraznov.userservice.repositories.UserRepository;
import by.baraznov.userservice.services.CardInfoService;
import by.baraznov.userservice.utils.CardAlreadyExist;
import by.baraznov.userservice.utils.CardNotFound;
import by.baraznov.userservice.utils.UserNotFound;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CardInfoServiceImpl implements CardInfoService {
    private final CardInfoRepository cardInfoRepository;
    private final UserRepository userRepository;
    private final CardGetDTOMapper cardGetDTOMapper;
    private final CardUpdateDTOMapper cardUpdateDTOMapper;
    private final CardCreateDTOMapper cardCreateDTOMapper;

    @Override
    @Transactional
    public CardGetDTO create(CardCreateDTO cardCreateDTO) {
        if(cardCreateDTO.userId() == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        CardInfo cardInfo = cardCreateDTOMapper.toEntity(cardCreateDTO);
        if(cardInfoRepository.existsByNumber(cardInfo.getNumber())){
            throw new CardAlreadyExist("Card number " + cardInfo.getNumber() + " already exist");
        }
        cardInfo.setUser(userRepository.findById(cardCreateDTO.userId())
                .orElseThrow(()-> new UserNotFound("User with id " + cardCreateDTO.userId() + " doesn't exist")));
        cardInfoRepository.save(cardInfo);
        return cardGetDTOMapper.toDto(cardInfo);
    }

    @Override
    public CardGetDTO getCardById(Integer id) {
        if(id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return cardGetDTOMapper.toDto(cardInfoRepository.findById(id)
                .orElseThrow(()-> new CardNotFound("Card with id " + id + " doesn't exist")));
    }

    @Override
    public List<CardGetDTO> getCardsByIds(List<Integer> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("List with ids cannot be null");
        }
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return cardGetDTOMapper.toDtos(cardInfoRepository.findCardsByIds(ids));
    }

    @Override
    public Page<CardGetDTO> getAllCards(Pageable pageable) {
        return cardInfoRepository.findAll(pageable).map(cardGetDTOMapper::toDto);
    }

    @Override
    @Transactional
    public CardGetDTO update(CardUpdateDTO cardUpdateDTO, Integer id) {
        if(id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        CardInfo cardInfo = cardInfoRepository.findById(id)
                .orElseThrow(()-> new CardNotFound("Card with id " + id + " doesn't exist"));
        if (cardUpdateDTO.number() != null && cardUpdateDTO.number().equals(cardInfo.getNumber())) {
            throw new CardAlreadyExist("Card number " + cardInfo.getNumber() + " already exist");
        }
        cardUpdateDTOMapper.merge(cardInfo, cardUpdateDTO);
        cardInfoRepository.save(cardInfo);
        return cardGetDTOMapper.toDto(cardInfo);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if(id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if(!cardInfoRepository.existsById(id)) {
            throw new CardNotFound("Card with id " + id + " doesn't exist");
        }
        cardInfoRepository.deleteById(id);
    }
}
