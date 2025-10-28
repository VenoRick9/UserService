package by.baraznov.userservice.read.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "users")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserQuery {
    @Id
    private String id;
    private String name;
    private String surname;
    private String email;
    private LocalDate birthDate;
    private List<CardInfoQuery> cards;

    public void addCard(CardInfoQuery card) {
        this.cards.add(card);
    }
}