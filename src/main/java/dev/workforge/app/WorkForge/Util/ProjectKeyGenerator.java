package dev.workforge.app.WorkForge.Util;

public class ProjectKeyGenerator {
    public static String generateKey(String name) {
        if (name == null || name.isBlank()) return "PRJ";
        int limitCounter  = 3;
        String[] words = name.trim().toUpperCase().split("\\s+");
        StringBuilder key = new StringBuilder();
        if (words.length > 1) {
            for (String word : words) {
                if (!word.isBlank()) {
                    key.append(word.charAt(0));
                }
            }
            return key.toString();
        }
        return name.substring(0, 2);
    }
}
