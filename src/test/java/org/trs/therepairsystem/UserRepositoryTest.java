package org.trs.therepairsystem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.trs.therepairsystem.entity.User;
import org.trs.therepairsystem.repository.UserRepository;

/**
 * UserRepositoryTest is a test class to verify database connectivity
 * by performing a simple save operation using the UserRepository.
 */
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testDbConnection() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("123456");

        userRepository.save(user);

        System.out.println("Database connection OK ✔");
    }
}
