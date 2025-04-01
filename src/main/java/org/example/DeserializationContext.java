package org.example;

import java.util.HashMap;
import java.util.Map;

public class DeserializationContext {
	private Map<Long, Object> context;

	public DeserializationContext() {
		this.context = new HashMap<>();
	}

	void register(Object o, long id) {
		context.put(id, o);
	}

	Object getObject(long id) {
		return context.getOrDefault(id, null);
	}
}
