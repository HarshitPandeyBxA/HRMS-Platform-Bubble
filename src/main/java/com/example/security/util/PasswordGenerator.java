package com.example.security.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordGenerator {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "@#$%&*!";
    private static final String ALL = UPPER + LOWER + DIGITS + SPECIAL;

    private static final int PASSWORD_LENGTH = 10;

    private final SecureRandom random = new SecureRandom();

    public String generateTempPassword() {

        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        // Ensure password policy
        password.append(randomChar(UPPER));
        password.append(randomChar(LOWER));
        password.append(randomChar(DIGITS));
        password.append(randomChar(SPECIAL));

        // Fill remaining characters
        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            password.append(randomChar(ALL));
        }

        return shuffle(password.toString());
    }

    private char randomChar(String source) {
        return source.charAt(random.nextInt(source.length()));
    }

    private String shuffle(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}
