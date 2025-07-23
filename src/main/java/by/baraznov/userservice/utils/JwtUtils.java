package by.baraznov.userservice.utils;


import by.baraznov.userservice.models.JwtAuthentication;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {

    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setLogin(claims.get("login", String.class));
        jwtInfoToken.setUserId(Integer.valueOf(claims.getSubject()));
        return jwtInfoToken;
    }


}
