package org.example;

import java.util.HashMap;
import java.util.Map;

public class SerializationContext {
	private final Map<Class<?>, Map<Object, Long>> serializedObjects;
	private final Map<Class<?>, Long> sequences;

	public SerializationContext() {
		serializedObjects = new HashMap<>();
		sequences = new HashMap<>();
	}

	public Map<Class<?>, Map<Object, Long>> getSerializedObjects() {
		return serializedObjects;
	}

	public Map<Class<?>, Long> getSequences() {
		return sequences;
	}

	public long registerObject(Object o) {
		Class<?> clazz = o.getClass();
		serializedObjects.putIfAbsent(clazz, new HashMap<>());
		sequences.putIfAbsent(clazz, 0L);

		Map<Object, Long> classSerializedObjects = serializedObjects.get(clazz);
		Long sequence = classSerializedObjects.get(o);

		if (sequence != null) {
			return sequence;
		}

		sequence = sequences.get(clazz);
		classSerializedObjects.put(o, sequence);
		sequences.put(clazz, sequence + 1);

		return sequence;
	}

	public Long getId(Object o) {
		Class<?> clazz = o.getClass();
		if (!serializedObjects.containsKey(clazz) || !serializedObjects.get(clazz).containsKey(o)) {
			return null;
		}
		return serializedObjects.get(clazz).get(o);
	}
}
