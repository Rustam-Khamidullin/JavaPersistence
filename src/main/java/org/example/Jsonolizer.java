package org.example;


import org.example.filter_conditions.PredicateFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Jsonolizer {
	static private final String IDENTITY_FIELD = "@";
	private JsonParser jsonParser = new JsonParser();
	private SerializationContext serializationContext;
	private DeserializationContext deserializationContext;
	static private final Set<Class<?>> simpleClasses = new HashSet<>(Arrays.asList(
			Boolean.class, Character.class, Byte.class, Short.class,
			Integer.class, Long.class, Float.class, Double.class,
			String.class, Void.class
	));

	public String objToJson(Object o) {
		if (o == null || isSimpleObject(o)) {
			throw new IllegalArgumentException("Can't serialize simple object.");
		}

		serializationContext = new SerializationContext();
		deserializationContext = new DeserializationContext();
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
		Long id = serializationContext.getId(o);
		if (id != null) {
			return id.toString();
		}

		Class<?> clazz = o.getClass();
		Field[] fields = clazz.getDeclaredFields();

		String prefix = "{";
		if (clazz.isAnnotationPresent(Identity.class)) {
			Identity annotation = clazz.getAnnotation(Identity.class);

			id = serializationContext.registerObject(o);

			prefix += "\"" + IDENTITY_FIELD + annotation.field() + "\" : " + id;

			if (fields.length != 0) {
				prefix += ",";
			}
		}

		return prefix + Arrays.stream(fields)
				.map(field -> {
					field.setAccessible(true);

					Object value;
					try {
						value = field.get(o);
					} catch (IllegalAccessException e) {
						throw new RuntimeException("Can't get value from field" + field.getName(), e);
					}

					return "\"" + field.getName() + "\": " + objToJsonField(value);
				}).collect(Collectors.joining(", ", "", "}"));
	}

	private static boolean isSimpleObject(Object o) {
		Class<?> clazz = o.getClass();
		return clazz.isPrimitive() || simpleClasses.contains(clazz);
	}

	/// /////////
	//using without filtering
	public Object jsonToObj(String json, Class<?> clazz) {
		return jsonToObj(json, clazz, PredicateFactory.createPredicateAlwaysTrue());
	}

	//for filtering
	public Object jsonToObj(String json, Class<?> clazz, Predicate<Map<String, String>> predicate) {
		if (simpleClasses.contains(clazz) || clazz.isPrimitive()) {
			return simpleObjectFromJson(json, clazz);
		}
		if (clazz.isArray()) {
			return jsonToArray(json, clazz, predicate);
		}
		return jsonToComplexObject(json, clazz, predicate);
	}

	private Object simpleObjectFromJson(String json, Class<?> clazz) {
		return switch (clazz.getSimpleName()) {
			case "String" -> json.substring(1, json.length() - 1);
			case "Boolean", "boolean" -> Boolean.parseBoolean(json);
			case "Character", "char" -> json.charAt(1);
			case "Byte", "byte" -> Byte.parseByte(json);
			case "Short", "short" -> Short.parseShort(json);
			case "Integer", "int" -> Integer.parseInt(json);
			case "Long", "long" -> Long.parseLong(json);
			case "Float", "float" -> Float.parseFloat(json);
			case "Double", "double" -> Double.parseDouble(json);
			case "Void" -> null;
			default -> throw new IllegalArgumentException("Method \"simpleObjectFromJson\" can convert only simple objects");
		};
    }

	private Object jsonToArray(String json, Class<?> clazz, Predicate<Map<String, String>> predicate) {
		Class<?> componentType = clazz.getComponentType();
		json = json.trim();
		if (!json.startsWith("[") || !json.endsWith("]")) {
			throw new IllegalArgumentException("Invalid JSON array: " + json);
		}

		json = json.substring(1, json.length() - 1).trim();

		if (json.isEmpty()) {
			return Array.newInstance(componentType, 0);
		}

		List<Object> elements = new ArrayList<>();
		StringBuilder currentElement = new StringBuilder();
		int braceCounter = 0; // для отслеживания вложенных объектов
		boolean inQuotes = false; // для отслеживания строковых значений

		for (int i = 0; i < json.length(); i++) {
			char c = json.charAt(i);
			if (c == '\"') {
				inQuotes = !inQuotes; // переключаем состояние в кавычках
			}
			if (!inQuotes) {
				if (c == '{') {
					braceCounter++;
				} else if (c == '}') {
					braceCounter--;
				}
				if (c == '[') {
					braceCounter++;
				} else if (c == ']') {
					braceCounter--;
				}
				if (c == ',' && braceCounter == 0) {
					// если мы находим запятую и не находимся внутри вложенных объектов или массива
					Object obj = jsonToObj(currentElement.toString().trim(), componentType, predicate);
					if (obj != null) {
						elements.add(obj);
					}
					currentElement.setLength(0); // очищаем текущий элемент
					continue;
				}
			}
			currentElement.append(c);
		}

		//последний элемент
		if (!currentElement.isEmpty()) {
			Object obj = jsonToObj(currentElement.toString().trim(), componentType, predicate);
			if (obj != null) {
				elements.add(obj);
			}
		}

		Object array = Array.newInstance(componentType, elements.size());
		for (int i = 0; i < elements.size(); i++) {
			Array.set(array, i, elements.get(i));
		}

		return array;
	}

	public Object jsonToComplexObject(String json, Class<?> clazz, Predicate<Map<String, String>> predicate) {
		if (Objects.equals(json, "null")) {
			return null;
		}

		try {
			boolean isIdentity = clazz.isAnnotationPresent(Identity.class);

			if (isIdentity) {
				try {
					long id = Long.parseLong(json);

					return deserializationContext.getObject(clazz, id);
				} catch (NumberFormatException ignored) {
				}
			}

			json = json.trim();
			if ((!json.startsWith("{") || !json.endsWith("}"))) {
				throw new IllegalArgumentException("Can't convert json to complex object: " + json);
			}

			Object instance = getInstance(clazz);

			json = json.substring(1, json.length() - 1).trim();
			Map<String, String> keyValues = jsonParser.parseToKeyValue(json);

			if (isIdentity) {
				Identity annotation = clazz.getAnnotation(Identity.class);

				String stringId = keyValues.get(IDENTITY_FIELD + annotation.field());

				long id = Long.parseLong(stringId);

				deserializationContext.register(instance, id);
			}

			if (json.isEmpty()) {
				return instance;
			}

			if (!predicate.test(keyValues)) {
				return null;
			}

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
}
