package org.trs.therepairsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.trs.therepairsystem.common.exception.BusinessException;
import org.trs.therepairsystem.entity.User;
import org.trs.therepairsystem.repository.RepairOrderRepository;
import org.trs.therepairsystem.repository.RoleRepository;
import org.trs.therepairsystem.repository.UserRepository;
import org.trs.therepairsystem.repository.UserRoleRelRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplPasswordTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRelRepository userRoleRelRepository;

    @Mock
    private RepairOrderRepository repairOrderRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void adminResetPassword_shouldEncodeAndSave() {
        User user = new User();
        user.setId(1L);
        user.setUsername("u1");
        user.setPassword("$2a$10$oldHashValuexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.adminResetPassword(1L, "Admin1234");

        verify(userRepository).save(user);
        assertNotEquals("Admin1234", user.getPassword());
        assertTrue(user.getPassword().startsWith("$2"));
    }

    @Test
    void changePassword_shouldThrowWhenOldPasswordWrong() {
        User user = new User();
        user.setId(2L);
        user.setUsername("u2");
        user.setPassword("$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.jqstwCa");

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.changePassword(2L, "wrongpass1", "Newpass123"));
        assertTrue(ex.getMessage().contains("旧密码错误"));
    }

    @Test
    void adminResetPassword_shouldThrowWhenUserMissing() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> userService.adminResetPassword(999L, "Admin1234"));
    }
}
