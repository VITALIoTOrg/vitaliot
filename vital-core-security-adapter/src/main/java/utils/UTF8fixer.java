package utils;

import java.io.UnsupportedEncodingException;

public class UTF8fixer {
    public static String convert(String s) {
        String converted;
        byte[] bytes = new byte[s.length()];

        for (int i = 0; i < s.length(); i++) {
            bytes[i] = (byte) s.charAt(i);
        }

        try {
            converted = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException ue) {
            converted = null;
        }

        return converted;
    }
}

