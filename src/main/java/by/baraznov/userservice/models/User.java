package by.baraznov.userservice.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.smartcardio.Card;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@Setter
@Getter
@RequiredArgsConstructor
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    @Size(min = 2, max = 15,message = "The name must contain between 2 and 15 characters")
    @NotBlank
    private String name;
    @Column(name = "surname")
    @Size(min = 2, max = 25,message = "The surname must contain between 2 and 25 characters")
    @NotBlank
    private String surname;
    @Column(name = "birth_date")
    @NotNull
    private LocalDate birthDate;
    @Column(name = "email")
    @Pattern(regexp = "^[A-Za-z0-9]+@[a-z]+\\.[a-z]+$")
    @NotBlank
    private String email;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CardInfo> cards;
}
