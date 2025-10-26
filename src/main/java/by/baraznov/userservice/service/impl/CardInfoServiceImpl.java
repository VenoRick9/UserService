package by.baraznov.userservice.service.impl;

import by.baraznov.userservice.dto.card.CardCreateDTO;
import by.baraznov.userservice.dto.card.CardGetDTO;
import by.baraznov.userservice.dto.card.CardUpdateDTO;
import by.baraznov.userservice.mapper.card.CardCreateDTOMapper;
import by.baraznov.userservice.mapper.card.CardGetDTOMapper;
import by.baraznov.userservice.mapper.card.CardUpdateDTOMapper;
import by.baraznov.userservice.model.CardInfo;
import by.baraznov.userservice.repository.CardInfoRepository;
import by.baraznov.userservice.repository.UserRepository;
import by.baraznov.userservice.service.CardInfoService;
import by.baraznov.userservice.util.CardAlreadyExist;
import by.baraznov.userservice.util.CardNotFound;
import by.baraznov.userservice.util.JwtUtils;
import by.baraznov.userservice.util.UserNotFound;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CardInfoServiceImpl implements CardInfoService {
    private final CardInfoRepository cardInfoRepository;
    private final UserRepository userRepository;
    private final CardGetDTOMapper cardGetDTOMapper;
    private final CardUpdateDTOMapper cardUpdateDTOMapper;
    private final CardCreateDTOMapper cardCreateDTOMapper;
    private final CacheManager cacheManager;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "user", key = "#result.userId()"),
                    @CacheEvict(cacheNames = "allUsers", allEntries = true)
            }
    )
    public CardGetDTO create(CardCreateDTO cardCreateDTO, String authentication) {
        String token = authentication.startsWith("Bearer ") ?
                authentication.substring(7) : authentication;
        UUID userId = jwtUtils.getAccessClaims(token);
        CardInfo cardInfo = cardCreateDTOMapper.toEntity(cardCreateDTO);
        if (cardInfoRepository.existsByNumber(cardInfo.getNumber())) {
            throw new CardAlreadyExist("Card number " + cardInfo.getNumber() + " already exist");
        }
        cardInfo.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound("User with id " + userId + " doesn't exist")));
        cardInfoRepository.save(cardInfo);
        return cardGetDTOMapper.toDto(cardInfo);
    }

    @Override
    public CardGetDTO getCardById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return cardGetDTOMapper.toDto(cardInfoRepository.findById(id)
                .orElseThrow(() -> new CardNotFound("Card with id " + id + " doesn't exist")));
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
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "user", key = "#result.userId()"),
                    @CacheEvict(cacheNames = "allUsers", allEntries = true)
            }
    )
    public CardGetDTO update(CardUpdateDTO cardUpdateDTO, Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        CardInfo cardInfo = cardInfoRepository.findById(id)
                .orElseThrow(() -> new CardNotFound("Card with id " + id + " doesn't exist"));
        if (cardUpdateDTO.number() != null && cardUpdateDTO.number().equals(cardInfo.getNumber())) {
            throw new CardAlreadyExist("Card number " + cardInfo.getNumber() + " already exist");
        }
        cardUpdateDTOMapper.merge(cardInfo, cardUpdateDTO);
        cardInfoRepository.save(cardInfo);
        return cardGetDTOMapper.toDto(cardInfo);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "allUsers", allEntries = true)
    public void delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (!cardInfoRepository.existsById(id)) {
            throw new CardNotFound("Card with id " + id + " doesn't exist");
        }
        CardInfo card = cardInfoRepository.findById(id)
                .orElseThrow(() -> new CardNotFound("Card with id " + id + " doesn't exist"));

        UUID userId = card.getUser().getId();
        cardInfoRepository.deleteById(id);
        Objects.requireNonNull(cacheManager.getCache("user")).evict(userId);
    }
}
