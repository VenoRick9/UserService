package by.baraznov.userservice.services;

import by.baraznov.userservice.dtos.card.CardCreateDTO;
import by.baraznov.userservice.dtos.card.CardGetDTO;
import by.baraznov.userservice.dtos.card.CardUpdateDTO;
import by.baraznov.userservice.mappers.card.CardCreateDTOMapper;
import by.baraznov.userservice.mappers.card.CardGetDTOMapper;
import by.baraznov.userservice.mappers.card.CardUpdateDTOMapper;
import by.baraznov.userservice.models.CardInfo;
import by.baraznov.userservice.models.User;
import by.baraznov.userservice.repositories.CardInfoRepository;
import by.baraznov.userservice.repositories.UserRepository;
import by.baraznov.userservice.services.impl.CardInfoServiceImpl;
import by.baraznov.userservice.utils.CardAlreadyExist;
import by.baraznov.userservice.utils.CardNotFound;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardInfoServiceTest {
    @Mock
    private CardInfoRepository cardInfoRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CardGetDTOMapper cardGetDTOMapper;
    @Mock
    private CardCreateDTOMapper cardCreateDTOMapper;
    @Mock
    private CardUpdateDTOMapper cardUpdateDTOMapper;
    @InjectMocks
    private CardInfoServiceImpl cardService;
    @Mock
    private CacheManager cacheManager;

    @Test
    public void test_getAllCards() {
        Pageable pageable = PageRequest.of(0, 2);
        User user = new User(1, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        CardInfo card1 = new CardInfo(1, "1234567812345678", "JOHN DOE",
                LocalDate.of(2030, 12, 31), user);
        CardInfo card2 = new CardInfo(2, "8765432187654321", "JANE DOE",
                LocalDate.of(2029, 11, 30), user);
        CardGetDTO dto1 = new CardGetDTO(1, user.getId(), "1234567812345678", "JOHN DOE",
                LocalDate.of(2030, 12, 31));
        CardGetDTO dto2 = new CardGetDTO(2, user.getId(), "8765432187654321", "JANE DOE",
                LocalDate.of(2029, 11, 30));
        Page<CardInfo> cardPage = new PageImpl<>(List.of(card1, card2), pageable, 2);
        when(cardInfoRepository.findAll(pageable)).thenReturn(cardPage);
        when(cardGetDTOMapper.toDto(card1)).thenReturn(dto1);
        when(cardGetDTOMapper.toDto(card2)).thenReturn(dto2);

        Page<CardGetDTO> result = cardService.getAllCards(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));
        verify(cardInfoRepository).findAll(pageable);
        verify(cardGetDTOMapper).toDto(card1);
        verify(cardGetDTOMapper).toDto(card2);
    }


    @Test
    void test_getCardById() {
        User user = new User(1, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        CardInfo card = new CardInfo(1, "1234567890123456", "JOHN DOE",
                LocalDate.of(2030, 1, 1), user);
        CardGetDTO dto = new CardGetDTO(1, user.getId(), "1234567890123456", "JOHN DOE",
                LocalDate.of(2030, 1, 1));
        when(cardInfoRepository.findById(1)).thenReturn(Optional.of(card));
        when(cardGetDTOMapper.toDto(card)).thenReturn(dto);

        CardGetDTO result = cardService.getCardById(1);

        assertEquals(dto, result);
        verify(cardInfoRepository).findById(1);
        verify(cardGetDTOMapper).toDto(card);
    }

    @Test
    void test_getCardsByIds() {
        User user = new User(1, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        CardInfo card1 = new CardInfo(1, "1111222233334444", "JOHN DOE",
                LocalDate.of(2030, 1, 1), user);
        CardInfo card2 = new CardInfo(2, "5555666677778888", "JANE SMITH",
                LocalDate.of(2031, 5, 1), user);
        List<CardInfo> cards = List.of(card1, card2);
        List<CardGetDTO> dtos = List.of(
                new CardGetDTO(1, user.getId(), "1111222233334444", "JOHN DOE",
                        LocalDate.of(2030, 1, 1)),
                new CardGetDTO(2, user.getId(), "5555666677778888", "JANE SMITH",
                        LocalDate.of(2031, 5, 1)));
        when(cardInfoRepository.findCardsByIds(List.of(1, 2))).thenReturn(cards);
        when(cardGetDTOMapper.toDtos(cards)).thenReturn(dtos);

        List<CardGetDTO> result = cardService.getCardsByIds(List.of(1, 2));

        assertEquals(2, result.size());
        verify(cardInfoRepository).findCardsByIds(List.of(1, 2));
        verify(cardGetDTOMapper).toDtos(cards);
    }

    @Test
    void test_createCard() {
        User user = new User(1, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        CardCreateDTO createDTO = new CardCreateDTO("1234567890123456", "JOHN DOE",
                LocalDate.of(2030, 1, 1));
        CardInfo cardInfo = new CardInfo(null, "1234567890123456", "JOHN DOE",
                LocalDate.of(2030, 1, 1), null);
        CardInfo savedCard = new CardInfo(1, "1234567890123456", "JOHN DOE",
                LocalDate.of(2030, 1, 1), user);
        CardGetDTO getDTO = new CardGetDTO(1, user.getId(), "1234567890123456", "JOHN DOE",
                LocalDate.of(2030, 1, 1));
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(1);
        when(cardCreateDTOMapper.toEntity(createDTO)).thenReturn(cardInfo);
        when(cardInfoRepository.existsByNumber(cardInfo.getNumber())).thenReturn(false);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(cardInfoRepository.save(cardInfo)).thenReturn(savedCard);
        when(cardGetDTOMapper.toDto(cardInfo)).thenReturn(getDTO);

        CardGetDTO result = cardService.create(createDTO, authentication);

        assertEquals(getDTO, result);
        verify(cardCreateDTOMapper).toEntity(createDTO);
        verify(cardInfoRepository).existsByNumber(cardInfo.getNumber());
        verify(userRepository).findById(1);
        verify(cardInfoRepository).save(cardInfo);
        verify(cardGetDTOMapper).toDto(cardInfo);
    }

    @Test
    void test_updateCard() {

        Integer cardId = 1;
        User user = new User(1, "John", "Doe",
                LocalDate.of(1990, 1, 1), "john@example.com", List.of());
        CardUpdateDTO updateDTO = new CardUpdateDTO("9999888877776666", null, null);
        CardInfo existing = new CardInfo(1, "1234567890123456", "JOHN DOE",
                LocalDate.of(2030, 1, 1), user);
        CardGetDTO getDTO = new CardGetDTO(1, user.getId(), "9999888877776666", "JOHN DOE",
                LocalDate.of(2030, 1, 1));
        when(cardInfoRepository.findById(cardId)).thenReturn(Optional.of(existing));
        doAnswer(invocation -> {
            CardInfo c = invocation.getArgument(0);
            c.setNumber("9999888877776666");
            return null;
        }).when(cardUpdateDTOMapper).merge(existing, updateDTO);
        when(cardInfoRepository.save(existing)).thenReturn(existing);
        when(cardGetDTOMapper.toDto(existing)).thenReturn(getDTO);

        CardGetDTO result = cardService.update(updateDTO, cardId);

        assertEquals(getDTO, result);
        assertEquals("9999888877776666", existing.getNumber());
        verify(cardInfoRepository).findById(cardId);
        verify(cardUpdateDTOMapper).merge(existing, updateDTO);
        verify(cardInfoRepository).save(existing);
        verify(cardGetDTOMapper).toDto(existing);
    }

    @Test
    void test_deleteCard() {
        Integer cardId = 1;
        CardInfo cardInfo = new CardInfo();
        User user = new User();
        user.setId(42);
        cardInfo.setUser(user);
        when(cardInfoRepository.existsById(cardId)).thenReturn(true);
        when(cardInfoRepository.findById(cardId)).thenReturn(Optional.of(cardInfo));
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache("user")).thenReturn(cache);
        doNothing().when(cache).evict(any());

        cardService.delete(cardId);

        verify(cardInfoRepository).existsById(cardId);
        verify(cardInfoRepository).deleteById(cardId);
    }

    @Test
    public void test_createCard_shouldThrowCardAlreadyExist() {
        Integer userId = 1;
        String cardNumber = "1234 5678 9012 3456";

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userId);

        CardCreateDTO cardCreateDTO = new CardCreateDTO(cardNumber, "JOHN FOG", LocalDate.now());
        CardInfo cardInfo = new CardInfo();
        cardInfo.setNumber(cardNumber);

        when(cardCreateDTOMapper.toEntity(cardCreateDTO)).thenReturn(cardInfo);
        when(cardInfoRepository.existsByNumber(cardNumber)).thenReturn(true);

        CardAlreadyExist exception = assertThrows(
                CardAlreadyExist.class,
                () -> cardService.create(cardCreateDTO, authentication)
        );

        assertTrue(exception.getMessage().contains("already exist"));
        verify(cardInfoRepository).existsByNumber(cardNumber);
        verify(cardCreateDTOMapper).toEntity(cardCreateDTO);
        verifyNoMoreInteractions(cardInfoRepository);
    }

    @Test
    public void test_getCardById_shouldThrowCardNotFound() {
        Integer cardId = 99;
        when(cardInfoRepository.findById(cardId)).thenReturn(Optional.empty());

        CardNotFound exception = assertThrows(
                CardNotFound.class,
                () -> cardService.getCardById(cardId)
        );

        assertEquals("Card with id " + cardId + " doesn't exist", exception.getMessage());
        verify(cardInfoRepository).findById(cardId);
    }


}


