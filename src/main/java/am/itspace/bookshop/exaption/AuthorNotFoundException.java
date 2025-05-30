package am.itspace.bookshop.exaption;

import jakarta.persistence.EntityNotFoundException;

public class AuthorNotFoundException extends EntityNotFoundException {
    public AuthorNotFoundException(String message) {
        super(message);
    }
}
