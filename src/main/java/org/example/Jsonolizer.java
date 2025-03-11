package org.example;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Jsonolizer {
    static private final Set<Class<?>> simpleClasses = new HashSet<>();

    static {
        simpleClasses.add(Boolean.class);
        simpleClasses.add(Character.class);
        simpleClasses.add(Byte.class);
        simpleClasses.add(Short.class);
        simpleClasses.add(Integer.class);
        simpleClasses.add(Long.class);
        simpleClasses.add(Float.class);
        simpleClasses.add(Double.class);
        simpleClasses.add(Void.class);
        simpleClasses.add(String.class);
    }

    public String objToJson(Object o) {
        if (isSimpleObject(o)) {
            throw new IllegalArgumentException("Can't serialize simple object.");
        }

        return objToJsonField(o);
    }

    public String objToJsonField(Object o) {
        if (o == null) {
            return "null";
        }

        Class<?> clazz = o.getClass();

        if (isSimpleObject(o)) {
            return simpleToJson(o);
        }

        if (clazz.isArray()) {
            return arrayToJson(o);
        }

        return realObjToJson(o);
    }

    public String simpleToJson(Object o) {
        if (o instanceof String) {
            return "\"" + o + "\"";
        }

        return o.toString();
    }

    public String arrayToJson(Object o) {
        if (o.getClass().getComponentType().isPrimitive()) {
            StringBuilder res = new StringBuilder("[");
            int length = Array.getLength(o);
            for (int i = 0; i < length; i++) {
                res.append(Array.get(o, i));
                if (i != length - 1) {
                    res.append(", ");
                }
            }
            res.append("]");
            return res.toString();
        }

        return Arrays.stream((Object[]) o)
                .map(this::objToJsonField)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private String realObjToJson(Object o) {
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

    private static boolean isSimpleObject(Object o) {
        Class<?> clazz = o.getClass();
        return clazz.isPrimitive() || simpleClasses.contains(clazz);
    }

    public static void main(String[] args) {
        String[][] a = new String[][]{{"a"}, {"b", "c"}};

        System.out.println(Arrays.deepToString(a));
    }
}
