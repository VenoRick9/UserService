package by.baraznov.userservice.controllers;

import by.baraznov.userservice.dtos.PageResponse;
import by.baraznov.userservice.dtos.card.CardCreateDTO;
import by.baraznov.userservice.dtos.card.CardGetDTO;
import by.baraznov.userservice.dtos.card.CardUpdateDTO;
import by.baraznov.userservice.services.CardInfoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cards")
@AllArgsConstructor
public class CardInfoController {
    private final CardInfoService cardInfoService;

    @GetMapping
    public ResponseEntity<PageResponse<CardGetDTO>> getAllCards(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(PageResponse.toPageResponse(cardInfoService.getAllCards(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardGetDTO> getCardById(@PathVariable("id") int id) {
        return ResponseEntity.ok(cardInfoService.getCardById(id));
    }

    @GetMapping(params = "ids")
    public ResponseEntity<List<CardGetDTO>> getCardsByIds(@RequestParam List<Integer> ids) {
        return ResponseEntity.ok(cardInfoService.getCardsByIds(ids));
    }

    @PostMapping
    public ResponseEntity<CardGetDTO> create(@RequestBody @Valid CardCreateDTO cardCreateDTO,
                                             Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardInfoService.create(cardCreateDTO, authentication));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CardGetDTO> update(@RequestBody @Valid CardUpdateDTO cardUpdateDTO,
                                             @PathVariable("id") int id) {
        return ResponseEntity.ok(cardInfoService.update(cardUpdateDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int id) {
        cardInfoService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
