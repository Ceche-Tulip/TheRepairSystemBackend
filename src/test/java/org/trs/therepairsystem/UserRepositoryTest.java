    package org.trs.therepairsystem;

    import java.util.UUID;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.test.annotation.Rollback;
    import org.springframework.transaction.annotation.Transactional;
    import org.trs.therepairsystem.entity.User;
    import org.trs.therepairsystem.repository.UserRepository;

    /**
     * UserRepositoryTest is a test class to verify database connectivity
     * by performing a simple save operation using the UserRepository.
     */
    @SpringBootTest
    @Transactional
    @Rollback
    public class UserRepositoryTest {

        @Autowired
        private UserRepository userRepository;

        @Test
        void testDbConnection() {
            String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12);

            User user = new User();
            user.setUsername("testuser_" + suffix);
            user.setPassword("123456");
            user.setRealName("测试用户");
            user.setPhone("13" + suffix.substring(0, 9));

            userRepository.save(user);

            System.out.println("Database connection OK ✔");
        }
    }
