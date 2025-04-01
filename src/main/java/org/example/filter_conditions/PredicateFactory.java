package org.example.filter_conditions;

import java.util.Map;
import java.util.function.Predicate;

public class PredicateFactory {

	public static Predicate<Map<String, String>> createPredicateMoreThan(String fieldName, int threshold) {
		return keyValues -> {
			String valueStr = keyValues.get(fieldName);
			if (valueStr != null) {
				try {
					int value = Integer.parseInt(valueStr);
					return value > threshold;
				} catch (NumberFormatException e) {
					throw new RuntimeException("Value for field '" + fieldName + "' is not a valid integer", e);
				}
			}
			return false; // Если значение отсутствует, возвращаем false
		};
	}
	public static Predicate<Map<String, String>> createPredicateAlwaysTrue() {
		return keyValues -> {
			return true;
		};
	}
}