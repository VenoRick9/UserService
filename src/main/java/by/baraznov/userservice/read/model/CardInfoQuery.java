package by.baraznov.userservice.read.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardInfoQuery {
    private Integer id;
    private String userId;
    private String number;
    private String holder;
    private LocalDate expirationDate;
}
