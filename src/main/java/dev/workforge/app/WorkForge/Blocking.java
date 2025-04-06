package dev.workforge.app.WorkForge;

import java.util.HashMap;
import java.util.Map;

public class Blocking {
    private final Map<String, Integer> map = new HashMap<>();

    public void put(String id) {
        map.computeIfAbsent(id,k -> 0);
    }

    public boolean checkIfPresent(String id) {
        return map.computeIfPresent(id, (k, v) -> v + 1) != null;
    }

    public boolean check(String id) {
        int LIMIT_COUNTER = 10;
        if (map.containsKey(id) && map.get(id) >= LIMIT_COUNTER) {
            return false;
        }
        return true;
    }
}
