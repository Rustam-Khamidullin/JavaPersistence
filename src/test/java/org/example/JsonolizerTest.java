package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class JsonolizerTest {
	Jsonolizer jsonolizer;
	@BeforeEach
	void setUp() {
		jsonolizer = new Jsonolizer();
	}

	@Test
	public void emptyObject() {
		Empty empty = new Empty();
		assertEquals("{}", jsonolizer.objToJson((empty)));
	}

	@Test
	public void primitiveTypes() {
		Object[] primitiveTypesArray = new Object[8];
		primitiveTypesArray[0] = 10;		// int
		primitiveTypesArray[1] = 20.5;		// double
		primitiveTypesArray[2] = 'A';		// char
		primitiveTypesArray[3] = true;		// boolean
		primitiveTypesArray[4] = (byte) 1;	// byte
		primitiveTypesArray[5] = (short) 2;	// short
		primitiveTypesArray[6] = 30L;		// long
		primitiveTypesArray[7] = 40.0f;		// float

		for (Object o : primitiveTypesArray) {
			assertThrows(IllegalArgumentException.class, () -> jsonolizer.objToJson(o));
		}
	}

	@Test
	public void primitiveObjects() {
		Object[] primitiveObjectsArray = new Object[8];
		primitiveObjectsArray[0] = Integer.valueOf(10);		// Integer
		primitiveObjectsArray[1] = Double.valueOf(20.5);		// Double
		primitiveObjectsArray[2] = Character.valueOf('A');	// Char
		primitiveObjectsArray[3] = Boolean.valueOf(true);	// Boolean
		primitiveObjectsArray[4] = Byte.valueOf((byte) 1);		// Byte
		primitiveObjectsArray[5] = Short.valueOf((short) 2);	// Short
		primitiveObjectsArray[6] = Long.valueOf(30L);		// Long
		primitiveObjectsArray[7] = Float.valueOf(40.0f);		// Float

		for (Object o : primitiveObjectsArray) {
			assertThrows(IllegalArgumentException.class, () -> jsonolizer.objToJson(o));
		}
	}

	//TODO: fix that
	@Test
	public void voidObject() {
		Void voidObject = null;
		assertThrows(IllegalArgumentException.class, () -> jsonolizer.objToJson(voidObject));
	}

	@Test
	public void integerObject() {
		OneFieldClass classForTest = new OneFieldClass(null);
		System.out.println(jsonolizer.objToJson(classForTest));

	}





}
