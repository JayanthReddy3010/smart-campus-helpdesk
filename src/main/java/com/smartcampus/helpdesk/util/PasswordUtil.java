package com.smartcampus.helpdesk.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public final class PasswordUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String FORMAT = "pbkdf2_sha256";
    private static final int ITERATIONS = 120_000;
    private static final int MAX_VERIFICATION_ITERATIONS = 1_000_000;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int SALT_LENGTH_BYTES = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hashPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank.");
        }

        byte[] salt = new byte[SALT_LENGTH_BYTES];
        RANDOM.nextBytes(salt);
        byte[] hash = derive(password, salt, ITERATIONS);
        return FORMAT + "$" + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }

        try {
            String[] parts = storedHash.split("\\$", -1);
            if (parts.length != 4 || !FORMAT.equals(parts[0])) {
                return false;
            }

            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);
            if (iterations < 10_000 || iterations > MAX_VERIFICATION_ITERATIONS
                    || salt.length < 16 || expectedHash.length == 0) {
                return false;
            }
            byte[] actualHash = derive(password, salt, iterations);
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static byte[] derive(String password, byte[] salt, int iterations) {
        char[] passwordChars = password.toCharArray();
        PBEKeySpec keySpec = new PBEKeySpec(passwordChars, salt, iterations, KEY_LENGTH_BITS);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            return factory.generateSecret(keySpec).getEncoded();
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Password hashing is unavailable.", exception);
        } finally {
            Arrays.fill(passwordChars, '\0');
            keySpec.clearPassword();
        }
    }
}
