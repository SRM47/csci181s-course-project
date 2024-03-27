package org.healthhaven.model;
import java.time.LocalDate;

public class ConcreteUserTest extends UserTest<User> {
    
    @Override
    public User createUser() {
        return new User("example@example.com", "password123", "John", "Doe", "123 Main St", LocalDate.of(1980, 1, 1));
    }
}
