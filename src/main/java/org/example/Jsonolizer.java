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
        if (o == null || isSimpleObject(o)) {
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
        if (o instanceof String || o instanceof Character) {
            return "\"" + o + "\"";
        }

        return o.toString();
    }

    public String arrayToJson(Object o) {
        if (o.getClass().getComponentType().isPrimitive()) {
            StringBuilder res = new StringBuilder("[");
            int length = Array.getLength(o);
            for (int i = 0; i < length; i++) {
                res.append(objToJsonField(Array.get(o, i)));
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

    /// /////////
    public Object jsonToObj(String json, Class<?> clazz) {
        if (simpleClasses.contains(clazz) || clazz.isPrimitive()) {
            return simpleObjectFromJson(json, clazz);
        }
        if (clazz.isArray()) {
            //TODO json to array
            return null;
        }
        return null; //TODO json to complex object
    }

    private Object simpleObjectFromJson(String json, Class<?> clazz) {
        if (clazz == String.class) {
            return json.substring(1, json.length() - 1); //bring string without "
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return Boolean.parseBoolean(json);
        }
        if (clazz == Character.class || clazz == char.class) {
            return json.charAt(1);
        }
        if (clazz == Byte.class || clazz == byte.class) {
            return Byte.parseByte(json);
        }
        if (clazz == Short.class || clazz == short.class) {
            return Short.parseShort(json);
        }
        if (clazz == Integer.class || clazz == int.class) {
            return Integer.parseInt(json);
        }
        if (clazz == Long.class || clazz == long.class) {
            return Long.parseLong(json);
        }
        if (clazz == Float.class || clazz == float.class) {
            return Float.parseFloat(json);
        }
        if (clazz == Double.class || clazz == double.class) {
            return Double.parseDouble(json);
        }
        if (clazz == Void.class) {
            return null;
        }
        throw new IllegalArgumentException("Method \"simpleObjectFromJson\" can convert only simple objects");
    }

    public static void main(String[] args) {
        String[][] a = new String[][]{{"a"}, {"b", "c"}};

        System.out.println(Arrays.deepToString(a));
    }
}
