package com.keunsori.keunsoriserver.global.util;

import com.keunsori.keunsoriserver.global.exception.RandomException;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.INITIALIZED_PASSWORD_GENERATION_FAILED;

public class RandomUtil {

    private static final String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String NUMERIC = "0123456789";
    private static final String SPECIAL = "!@#$%^&*";
    private static final String ALL_CHARACTERS = ALPHA + NUMERIC + SPECIAL;

    private static final int NEW_PASSWORD_LENGTH = 10;

    public static String generateRandomPassword() {
        try {
            SecureRandom random = SecureRandom.getInstanceStrong();

            StringBuilder randomString = new StringBuilder();

            // 최소 1개의 알파벳, 숫자, 특수문자 추가
            randomString.append(ALPHA.charAt(random.nextInt(ALPHA.length())));
            randomString.append(NUMERIC.charAt(random.nextInt(NUMERIC.length())));
            randomString.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

            // 나머지 부분은 랜덤하게 선택
            for (int i = 3; i < NEW_PASSWORD_LENGTH; i++) {
                randomString.append(ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())));
            }

            // 랜덤 문자열 섞기
            StringBuilder shuffledString = new StringBuilder();
            while (!randomString.isEmpty()) {
                int index = random.nextInt(randomString.length());
                shuffledString.append(randomString.charAt(index));
                randomString.deleteCharAt(index);
            }

            return shuffledString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RandomException(INITIALIZED_PASSWORD_GENERATION_FAILED);
        }
    }
}
