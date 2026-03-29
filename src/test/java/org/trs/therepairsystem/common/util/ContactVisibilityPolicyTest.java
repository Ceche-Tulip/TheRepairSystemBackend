package org.trs.therepairsystem.common.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.trs.therepairsystem.entity.RepairOrder;
import org.trs.therepairsystem.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContactVisibilityPolicyTest {

    private User submitter;
    private User engineer;
    private User outsider;
    private RepairOrder order;

    @BeforeEach
    void setUp() {
        submitter = new User();
        submitter.setId(1L);
        submitter.setPhone("13800138000");

        engineer = new User();
        engineer.setId(2L);
        engineer.setPhone("13900139000");

        outsider = new User();
        outsider.setId(3L);
        outsider.setPhone("13700137000");

        order = RepairOrder.builder()
                .id(100L)
                .submitUser(submitter)
                .engineer(engineer)
                .build();
    }

    @Test
    void adminShouldSeeFullPhone() {
        String phone = ContactVisibilityPolicy.resolveOrderPhone(engineer, order, 99L, true);
        assertEquals("13900139000", phone);
    }

    @Test
    void selfShouldSeeFullPhone() {
        String phone = ContactVisibilityPolicy.resolveOrderPhone(submitter, order, 1L, false);
        assertEquals("13800138000", phone);
    }

    @Test
    void collaboratorShouldSeeFullPhone() {
        String phone = ContactVisibilityPolicy.resolveOrderPhone(engineer, order, 1L, false);
        assertEquals("13900139000", phone);
    }

    @Test
    void outsiderShouldSeeMaskedPhone() {
        String phone = ContactVisibilityPolicy.resolveOrderPhone(engineer, order, outsider.getId(), false);
        assertEquals("139****9000", phone);
    }
}
