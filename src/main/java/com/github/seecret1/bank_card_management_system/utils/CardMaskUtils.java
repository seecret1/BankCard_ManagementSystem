package com.github.seecret1.bank_card_management_system.utils;

public class CardMaskUtils {

    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return cardNumber;
        }

        // Показываем только последние 4 цифры
        String lastFour = cardNumber.substring(cardNumber.length() - 4);

        // Маскируем остальные цифры
        return "**** **** **** " + lastFour;
    }

    public static String maskCardNumber(String cardNumber, String maskChar, int visibleDigits) {
        if (cardNumber == null || cardNumber.length() <= visibleDigits) {
            return cardNumber;
        }

        int maskLength = cardNumber.length() - visibleDigits;
        StringBuilder masked = new StringBuilder();

        for (int i = 0; i < maskLength; i++) {
            masked.append(maskChar);
            // Добавляем пробел после каждых 4 символов для читаемости
            if ((i + 1) % 4 == 0 && i < maskLength - 1) {
                masked.append(" ");
            }
        }

        // Добавляем пробел перед видимыми цифрами, если нужно
        if (masked.length() > 0 && masked.charAt(masked.length() - 1) != ' ') {
            masked.append(" ");
        }

        // Добавляем видимые цифры
        String visiblePart = cardNumber.substring(maskLength);
        for (int i = 0; i < visiblePart.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                masked.append(" ");
            }
            masked.append(visiblePart.charAt(i));
        }

        return masked.toString();
    }
}