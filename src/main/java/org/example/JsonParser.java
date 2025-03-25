package org.example;

import org.w3c.dom.ls.LSOutput;

import java.util.HashMap;
import java.util.Map;

public class JsonParser {
    JsonParser() {

    }

    public Map<String, String> parseToKeyValue(String json) {
        Map<String, String> map = new HashMap<>();
        int braceCounter = 0;   // { - objects
        int bracketCounter = 0; // [ - arrays
        boolean inQuotes = false; // "" block

        char[] chars = json.toCharArray();
        StringBuilder pair = new StringBuilder();
        for (int i = 0; i < json.length(); i++) {
            char c = chars[i];
            switch (c) {
                case '\\':
                    if (i + 1 < json.length() && json.charAt(i + 1) == '"') {
                        pair.append('"');
                        i++;
                    }
                    continue;
                case '\"':
                    inQuotes = !inQuotes;
                    break;
                case '{':
                    braceCounter++;
                    break;
                case '}':
                    braceCounter--;
                    break;
                case '[':
                    bracketCounter++;
                    break;
                case ']':
                    bracketCounter--;
                    break;
            }
            if (c == ',' && !inQuotes && braceCounter == 0 && bracketCounter == 0) {
                addPairToMap(pair.toString(), map);
                pair.setLength(0);
            } else {
                pair.append(c);
            }
        }
        if (!pair.isEmpty()) {
            addPairToMap(pair.toString(), map);
        }
        return map;
    }

    private void addPairToMap(String pair, Map<String, String> map) {
        String[] keyValue = pair.split("\\s*:\\s*", 2); // Разбиваем только по первой ":"
        if (keyValue.length == 2) {
            String key = keyValue[0].trim().replaceAll("^\"|\"$", ""); // remove start " and end "
            String value = keyValue[1].trim();
            map.put(key, value);
        }
    }

    public static void main(String[] args) {

    }

}
