package com.smartcampus.helpdesk.util;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class PasswordUtilTest {

    @Test
    void hashesAndVerifiesPasswordWithoutStoringPlaintext() {
        String password = "CampusPassword123!";
        String firstHash = PasswordUtil.hashPassword(password);
        String secondHash = PasswordUtil.hashPassword(password);

        assertNotEquals(password, firstHash);
        assertNotEquals(firstHash, secondHash);
        assertTrue(PasswordUtil.verifyPassword(password, firstHash));
        assertFalse(PasswordUtil.verifyPassword("wrong-password", firstHash));
    }
}
