package by.baraznov.userservice.utils.jwt;

public class JwtMalformedException extends RuntimeException {
    public JwtMalformedException(String message) {
        super(message);
    }
}
