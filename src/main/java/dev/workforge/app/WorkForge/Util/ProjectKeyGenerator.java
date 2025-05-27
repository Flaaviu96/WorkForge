package dev.workforge.app.WorkForge.Util;

public class ProjectKeyGenerator {
    public static String generateKey(String name) {
        if (name == null || name.isBlank()) return "PRJ";

        String[] words = name.trim().toUpperCase().split("\\s+");
        StringBuilder key = new StringBuilder();
        for (String word : words) {
            if (!word.isBlank()) {
                key.append(word.charAt(0));
            }
        }

        return key.toString();
    }
}
