package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
		primitiveTypesArray[0] = 10;        // int
		primitiveTypesArray[1] = 20.5;        // double
		primitiveTypesArray[2] = 'A';        // char
		primitiveTypesArray[3] = true;        // boolean
		primitiveTypesArray[4] = (byte) 1;    // byte
		primitiveTypesArray[5] = (short) 2;    // short
		primitiveTypesArray[6] = 30L;        // long
		primitiveTypesArray[7] = 40.0f;        // float

		for (Object o : primitiveTypesArray) {
			assertThrows(IllegalArgumentException.class, () -> jsonolizer.objToJson(o));
		}
	}

	@Test
	public void primitiveObjects() {
		Object[] primitiveObjectsArray = new Object[8];
		primitiveObjectsArray[0] = Integer.valueOf(10);        // Integer
		primitiveObjectsArray[1] = Double.valueOf(20.5);        // Double
		primitiveObjectsArray[2] = Character.valueOf('A');    // Char
		primitiveObjectsArray[3] = Boolean.valueOf(true);    // Boolean
		primitiveObjectsArray[4] = Byte.valueOf((byte) 1);        // Byte
		primitiveObjectsArray[5] = Short.valueOf((short) 2);    // Short
		primitiveObjectsArray[6] = Long.valueOf(30L);        // Long
		primitiveObjectsArray[7] = Float.valueOf(40.0f);        // Float

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
	public void nullField() {
		assertEquals("{\"val\": null}", jsonolizer.objToJson(new OnePrimitiveFieldClass(null)));
	}

	@Test
	public void integerFields() {
		//integer
		assertEquals("{\"val\": 42}", jsonolizer.objToJson(new OnePrimitiveFieldClass(42)));
		assertEquals("{\"val\": -1000}", jsonolizer.objToJson(new OnePrimitiveFieldClass(-1000)));
		assertEquals("{\"val\": 0}", jsonolizer.objToJson(new OnePrimitiveFieldClass(0)));
		//byte
		assertEquals("{\"val\": 42}", jsonolizer.objToJson(new OnePrimitiveFieldClass((byte) 42)));
		assertEquals("{\"val\": -100}", jsonolizer.objToJson(new OnePrimitiveFieldClass((byte) -100)));
		assertEquals("{\"val\": 0}", jsonolizer.objToJson(new OnePrimitiveFieldClass((byte) 0)));
		//short
		assertEquals("{\"val\": 42}", jsonolizer.objToJson(new OnePrimitiveFieldClass((short) 42)));
		assertEquals("{\"val\": -1000}", jsonolizer.objToJson(new OnePrimitiveFieldClass((short) -1000)));
		assertEquals("{\"val\": 0}", jsonolizer.objToJson(new OnePrimitiveFieldClass((short) 0)));
		//long
		assertEquals("{\"val\": 42}", jsonolizer.objToJson(new OnePrimitiveFieldClass(42L)));
		assertEquals("{\"val\": -1000}", jsonolizer.objToJson(new OnePrimitiveFieldClass(-1000L)));
		assertEquals("{\"val\": 0}", jsonolizer.objToJson(new OnePrimitiveFieldClass(0L)));
	}

	@Test
	public void fractionFields() {
		//double
		assertEquals("{\"val\": 22.1234567}", jsonolizer.objToJson(new OnePrimitiveFieldClass(22.1234567)));
		assertEquals("{\"val\": 0.0}", jsonolizer.objToJson(new OnePrimitiveFieldClass(0.0)));
		assertEquals("{\"val\": -22.1234567}", jsonolizer.objToJson(new OnePrimitiveFieldClass(-22.1234567)));
		//float
		assertEquals("{\"val\": 22.123}", jsonolizer.objToJson(new OnePrimitiveFieldClass((float) 22.123)));
		assertEquals("{\"val\": 0.0}", jsonolizer.objToJson(new OnePrimitiveFieldClass((float) 0.0)));
		assertEquals("{\"val\": -22.123}", jsonolizer.objToJson(new OnePrimitiveFieldClass((float) -22.123)));
	}

	@Test
	public void booleanFields() {
		assertEquals("{\"val\": true}", jsonolizer.objToJson(new OnePrimitiveFieldClass(true)));
		assertEquals("{\"val\": false}", jsonolizer.objToJson(new OnePrimitiveFieldClass(false)));
	}

	//TODO fix that
	@Test
	public void stringFields() {
		assertEquals("{\"val\": \"string\"}", jsonolizer.objToJson(new OnePrimitiveFieldClass("string")));
		assertEquals("{\"val\": \"\"}", jsonolizer.objToJson(new OnePrimitiveFieldClass("")));
		assertEquals("{\"val\": \"t\"}", jsonolizer.objToJson(new OnePrimitiveFieldClass(Character.valueOf('t'))));
		assertEquals("{\"val\": \"п\"}", jsonolizer.objToJson(new OnePrimitiveFieldClass(Character.valueOf('п'))));
		assertEquals("{\"val\": \"t\"}", jsonolizer.objToJson(new OnePrimitiveFieldClass('t')));
		assertEquals("{\"val\": \"п\"}", jsonolizer.objToJson(new OnePrimitiveFieldClass('п')));

		/**
		 * На это я думаю забить можно, служебные символы не так нужно видеть зрительно,
		 * в gson реализовано также как в этом тесте, что служебные символы через unicode \\u отображаются
		 **/
		assertEquals("{\"val\": \"\u0003\"}", jsonolizer.objToJson(new OnePrimitiveFieldClass((char) 3)));
	}

	@Test
	public void primitiveObjectsArrays() {
		Integer[] arrInteger = new Integer[]{1, 2, 3};
		assertEquals("[1, 2, 3]", jsonolizer.objToJson(arrInteger));

		Double[] arrDouble = new Double[]{1.0, 2.0, 3.0};
		assertEquals("[1.0, 2.0, 3.0]", jsonolizer.objToJson(arrDouble));

		Boolean[] arrBoolean = new Boolean[]{true, false};
		assertEquals("[true, false]", jsonolizer.objToJson(arrBoolean));

		Object[] arrObject = new Object[]{"a", "b", 1, 2, 1.0, 2.0, true, false, null};
		assertEquals("[\"a\", \"b\", 1, 2, 1.0, 2.0, true, false, null]", jsonolizer.objToJson(arrObject));

		String[] arrString = new String[]{"a", "b", "c"};
		assertEquals("[\"a\", \"b\", \"c\"]", jsonolizer.objToJson(arrString));

		char[] arrChar = new char[]{'a', 'b', 'c'};
		assertEquals("[\"a\", \"b\", \"c\"]", jsonolizer.objToJson(arrChar));
	}

	@Test
	public void complexObjectsArrays() {
		OneArrayFieldClass o =
				new OneArrayFieldClass(new Object[]{"a", "b", 1, 2, 1.0, 2.0, true, false, null});
		assertEquals("{\"arr\": [\"a\", \"b\", 1, 2, 1.0, 2.0, true, false, null]}", jsonolizer.objToJson(o));
	}


	@Test
	public void simpleObjectsFromJson() {
		/*string*/
		assertEquals("string", (String) jsonolizer.jsonToObj("\"string\"", String.class));
		/*boolean*/
		assertEquals(Boolean.valueOf(true), (Boolean) jsonolizer.jsonToObj("true", Boolean.class));
		assertEquals(Boolean.valueOf(false), (Boolean) jsonolizer.jsonToObj("false", Boolean.class));
		assertEquals(true, (boolean) jsonolizer.jsonToObj("true", boolean.class));
		assertEquals(false, (boolean) jsonolizer.jsonToObj("false", boolean.class));
		/*char*/
		assertEquals('r', (Character) jsonolizer.jsonToObj("\"r\"", Character.class));
		assertEquals('п', (Character) jsonolizer.jsonToObj("\"п\"", Character.class));
		/*byte*/
		assertEquals(Byte.valueOf((byte) 5), (Byte) jsonolizer.jsonToObj("5", Byte.class));
		assertEquals((byte) 5, (byte) jsonolizer.jsonToObj("5", byte.class));
		/*short*/
		assertEquals((short) 5, (Short) jsonolizer.jsonToObj("5", Short.class));
		assertEquals((short) 5, (short) jsonolizer.jsonToObj("5", short.class));
		/*integer*/
		assertEquals(5, (Integer) jsonolizer.jsonToObj("5", Integer.class));
		assertEquals(5, (int) jsonolizer.jsonToObj("5", int.class));
		/*long*/
		assertEquals(5, (Long) jsonolizer.jsonToObj("5", Long.class));
		assertEquals(5, (long) jsonolizer.jsonToObj("5", long.class));
		/*double*/
		assertEquals(Double.valueOf(5.0), (Double) jsonolizer.jsonToObj("5.0", Double.class));
		assertEquals((double) 5.0, (double) jsonolizer.jsonToObj("5.0", double.class));
		/*float*/
		assertEquals(Float.valueOf(5.0F), (Float) jsonolizer.jsonToObj("5.0", Float.class));
		assertEquals(5.0F, (float) jsonolizer.jsonToObj("5.0", float.class));
		/*void*/
		assertNull(jsonolizer.jsonToObj("null", Void.class));
	}

}
