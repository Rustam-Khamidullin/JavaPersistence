package org.example;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Jsonolizer {

    public String objToJson(Object o) {
        Class<?> clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();

        return Arrays.stream(fields)
                .map(field -> {
                    field.setAccessible(true);

                    Object value;
                    try {
                        value = field.get(o);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Can't get value from field" + field.getName(), e);
                    }

                    return "\"" + field.getName() + "\": " + objToJsonField(value);
                }).collect(Collectors.joining(", ", "{", "}"));
    }

    public String objToJsonField(Object o) {
        return o.toString();
    }
}
