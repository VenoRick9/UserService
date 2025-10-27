package by.baraznov.userservice.util;

import by.baraznov.userservice.write.model.UserCommand;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtUtilTest {
    private final SecretKey jwtAccessSecret;
    public JwtUtilTest(
            @Value("${jwt.secret.access}") String jwtAccessSecret
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
    }
    public String generateToken(UserCommand user) {
        Instant expirationInstant = LocalDateTime.now()
                .plusMinutes(10)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setExpiration(Date.from(expirationInstant))
                .signWith(jwtAccessSecret)
                .compact();
    }
}