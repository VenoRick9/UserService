package by.baraznov.userservice.util;

public class CardAlreadyExist extends RuntimeException {
    public CardAlreadyExist(String message) {
        super(message);
    }
}
