package org.trs.therepairsystem.common.util;

public final class DataMaskingUtils {

    private DataMaskingUtils() {
    }

    public static String maskPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return phone;
        }
        String digitsOnly = phone.replaceAll("\\D", "");
        if (digitsOnly.length() < 7) {
            return "****";
        }
        return digitsOnly.substring(0, 3) + "****" + digitsOnly.substring(digitsOnly.length() - 4);
    }
}
