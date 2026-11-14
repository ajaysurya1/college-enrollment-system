package com.university.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    public void testHashAndVerifyPassword() {
        String password = "mySecretPassword123";
        String hashed = PasswordUtil.hashPassword(password);

        assertNotNull(hashed);
        assertNotEquals(password, hashed);
        assertTrue(hashed.startsWith("$2a$") || hashed.startsWith("$2b$"));

        assertTrue(PasswordUtil.checkPassword(password, hashed));
        assertFalse(PasswordUtil.checkPassword("wrongPassword", hashed));
    }

    @Test
    public void testEmptyPasswordThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.hashPassword("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.hashPassword(null);
        });
    }

    @Test
    public void testInvalidPasswordCheck() {
        assertFalse(PasswordUtil.checkPassword("abc", null));
        assertFalse(PasswordUtil.checkPassword(null, "hash"));
    }
}

