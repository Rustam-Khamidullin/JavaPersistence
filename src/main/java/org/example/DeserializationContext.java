package org.example;

import java.util.HashMap;
import java.util.Map;

public class DeserializationContext {
	private Map<Class<?>, Map<Long, Object>> context;

	public DeserializationContext() {
		this.context = new HashMap<>();
	}

	void register(Object o, long id) {
		Class<?> clazz = o.getClass();
		context.putIfAbsent(clazz, new HashMap<>());


		context.get(clazz).put(id, o);
	}

	Object getObject(Class<?> clazz, long id) {
		if (!context.containsKey(clazz)) {
			return null;
		}

		return context.get(clazz).getOrDefault(id, null);
	}
}
