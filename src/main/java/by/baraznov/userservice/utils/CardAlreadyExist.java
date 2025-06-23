package by.baraznov.userservice.utils;

public class CardAlreadyExist extends RuntimeException {
    public CardAlreadyExist(String message) {
        super(message);
    }
}
