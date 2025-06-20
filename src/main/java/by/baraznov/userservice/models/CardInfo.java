package by.baraznov.userservice.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "card_info")
@Setter
@Getter
@RequiredArgsConstructor
public class CardInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "number")
    @Size(min = 16, max = 16)
    @NotNull
    private Integer number;
    @Column(name = "holder")
    @Pattern(regexp = "^[A-Z]{1,25}\\s[A-Z]{1,15}$")
    @NotBlank
    private String holder;
    @Column(name = "expiration_date")
    @NotNull
    private LocalDate expirationDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
