package by.baraznov.userservice.controller;

import by.baraznov.userservice.dto.PageResponse;
import by.baraznov.userservice.dto.card.CardCreateDTO;
import by.baraznov.userservice.dto.card.CardGetDTO;
import by.baraznov.userservice.dto.card.CardUpdateDTO;
import by.baraznov.userservice.mediator.Mediator;
import by.baraznov.userservice.read.query.GetAllCardsQuery;
import by.baraznov.userservice.read.query.GetCardByIdQuery;
import by.baraznov.userservice.read.query.GetCardsByIdsQuery;
import by.baraznov.userservice.write.command.CreateCardCommand;
import by.baraznov.userservice.write.command.DeleteCardCommand;
import by.baraznov.userservice.write.command.UpdateCardCommand;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cards")
@AllArgsConstructor
public class CardInfoController {
    private final Mediator mediator;

    @GetMapping
    public ResponseEntity<PageResponse<CardGetDTO>> getAllCards(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(PageResponse.toPageResponse(mediator.send(GetAllCardsQuery.toQuery(pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardGetDTO> getCardById(@PathVariable("id") int id) {
        return ResponseEntity.ok(mediator.send(GetCardByIdQuery.toQuery(id)));
    }

    @GetMapping(params = "ids")
    public ResponseEntity<List<CardGetDTO>> getCardsByIds(@RequestParam List<Integer> ids) {
        return ResponseEntity.ok(mediator.send(GetCardsByIdsQuery.toQuery(ids)));
    }

    @PostMapping
    public ResponseEntity<CardGetDTO> create(@RequestBody @Valid CardCreateDTO cardCreateDTO,
                                             @RequestHeader("Authorization") String authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediator.send(
                CreateCardCommand.toCommand(cardCreateDTO.number(),cardCreateDTO.holder(),
                        cardCreateDTO.expirationDate(), authentication)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CardGetDTO> update(@RequestBody @Valid CardUpdateDTO cardUpdateDTO,
                                             @PathVariable("id") int id) {
        return ResponseEntity.ok(mediator.send(UpdateCardCommand.toCommand(id, cardUpdateDTO.number(),
                cardUpdateDTO.holder(), cardUpdateDTO.expirationDate())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int id) {
        mediator.send(DeleteCardCommand.toCommand(id));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
