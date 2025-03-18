package org.example;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
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

    /// /////////
    public Object jsonToObj(String json, Class<?> clazz) {
        if (simpleClasses.contains(clazz) || clazz.isPrimitive()) {
            return simpleObjectFromJson(json, clazz);
        }
        if (clazz.isArray()) {
            return jsonToArray(json, clazz);
        }
        return jsonToComplexObject(json, clazz); //TODO json to complex object
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

    private Object jsonToArray(String json, Class<?> clazz) {
        Class<?> componentType = clazz.getComponentType();
        json = json.trim();
        if (!json.startsWith("[") || !json.endsWith("]")) {
            throw new IllegalArgumentException("Invalid JSON array: " + json);
        }

        json = json.substring(1, json.length() - 1).trim();

        if (json.isEmpty()) {
            return Array.newInstance(componentType, 0);
        }
        String[] elements = json.split(",");
        Object array = Array.newInstance(componentType, elements.length);

        for (int i = 0; i < elements.length; i++) {
            Object element = jsonToObj(elements[i].trim(), componentType);
            Array.set(array, i, element);
        }

        return array;
    }

    private Object jsonToComplexObject(String json, Class<?> clazz) {
        try {
            Object instance = getInstance(clazz);

            json = json.trim();
            if (!json.startsWith("{") || !json.endsWith("}")) {
                throw new IllegalArgumentException("Can't convert json to complex object: " + json);
            }
            json = json.substring(1, json.length() - 1).trim();
            if (json.isEmpty()) {
                return instance;
            }

            JsonParser parser = new JsonParser();
            Map<String, String> keyValues = parser.parseToKeyValue(json);

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (keyValues.containsKey(field.getName())) {
                    Object value = jsonToObj(keyValues.get(field.getName()), field.getType());
                    field.set(instance, value);
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize JSON", e);
        }
    }

    private static Object getInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors(); //try to find any constructors
        if (constructors.length == 0) {
            throw new RuntimeException("No constructors found for class " + clazz.getName());
        }
        Constructor<?> constructor = constructors[0];//bring first
        constructor.setAccessible(true);
        Object[] args = new Object[constructor.getParameterCount()];
        return constructor.newInstance(args);
    }


    public static void main(String[] args) {
        String[][] a = new String[][]{{"a"}, {"b", "c"}};

        System.out.println(Arrays.deepToString(a));
    }
}
