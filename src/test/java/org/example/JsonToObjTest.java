package org.example;

import org.example.classes.*;
import org.example.filter_conditions.PredicateFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class JsonToObjTest {
    Jsonolizer jsonolizer;

    @BeforeEach
    void setUp() {
        jsonolizer = new Jsonolizer();
    }
    @Test
    public void jsonToSimpleObject() {
        /*string*/
        assertEquals("string", (String) jsonolizer.jsonToObj("\"string\"", String.class));
        /*boolean*/
        assertEquals(Boolean.TRUE, jsonolizer.jsonToObj("true", Boolean.class));
        assertEquals(Boolean.FALSE, jsonolizer.jsonToObj("false", Boolean.class));
        assertTrue((boolean) jsonolizer.jsonToObj("true", boolean.class));
        assertFalse((boolean) jsonolizer.jsonToObj("false", boolean.class));
        /*char*/
        assertEquals('r', (Character) jsonolizer.jsonToObj("\"r\"", Character.class));
        assertEquals('п', (Character) jsonolizer.jsonToObj("\"п\"", Character.class));
        assertEquals('r', (char) jsonolizer.jsonToObj("\"r\"", char.class));
        assertEquals('п', (char) jsonolizer.jsonToObj("\"п\"", char.class));
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
        assertEquals(5.0, (double) jsonolizer.jsonToObj("5.0", double.class));
        /*float*/
        assertEquals(Float.valueOf(5.0F), (Float) jsonolizer.jsonToObj("5.0", Float.class));
        assertEquals(5.0F, (float) jsonolizer.jsonToObj("5.0", float.class));
        /*void*/
        assertNull(jsonolizer.jsonToObj("null", Void.class));
    }

    @Test
    public void jsonToSimpleArray() {
        assertArrayEquals(new int[]{}, (int[]) jsonolizer.jsonToObj("[]", int[].class));
        assertArrayEquals(new int[]{1, 2, 3}, (int[]) jsonolizer.jsonToObj("[1,2,3]", int[].class));
    }

    @Test
    public void jsonToComplexArray() {
        String json = "[{\"str\": \"string1\"}, {\"str\": \"string2\"}]";
        OneStringClass[] result = (OneStringClass[]) jsonolizer.jsonToObj(json, OneStringClass[].class);
        assertEquals("string1", result[0].str);
        assertEquals("string2", result[1].str);

        json = "[{\"strs\": [\"string1\", \"string2\"]}, {\"strs\": [\"string3\", \"string4\"]}]";
        OneStringArrayFieldClass[] res = (OneStringArrayFieldClass[]) jsonolizer.jsonToObj(json, OneStringArrayFieldClass[].class);
        assertEquals("string1", res[0].strs[0]);
        assertEquals("string2", res[0].strs[1]);
        assertEquals("string3", res[1].strs[0]);
        assertEquals("string4", res[1].strs[1]);
    }

    @Test
    public void complexObject() {
        Empty empty = (Empty)jsonolizer.jsonToObj("{}", Empty.class);
        assertEquals(0, empty.getClass().getDeclaredFields().length);
    }

    @Test
    public void testTrueFalsePredicate() {
        String json = "{\"field1\": 1, \"field2\": 2}";
        Predicate<Map<String, String>> predicate = PredicateFactory.createPredicateAlwaysTrue();
        TwoIntegerClass twoIntegerClass = (TwoIntegerClass) jsonolizer.jsonToObj(json, TwoIntegerClass.class, predicate);
        assertEquals(1, twoIntegerClass.field1);
        assertEquals(2, twoIntegerClass.field2);

        predicate = PredicateFactory.createPredicateAlwaysFalse();
        twoIntegerClass = (TwoIntegerClass) jsonolizer.jsonToObj(json, TwoIntegerClass.class, predicate);
        assertNull(twoIntegerClass);
    }

    @Test
    public void testMoreLessThanPredicate() {
        String json = "{\"field1\": 1, \"field2\": 2}";
        Predicate<Map<String, String>> predicate = PredicateFactory.createPredicateMoreThan("field1", 0);
        TwoIntegerClass twoIntegerClass = (TwoIntegerClass) jsonolizer.jsonToObj(json, TwoIntegerClass.class, predicate);
        assertEquals(1, twoIntegerClass.field1);
        assertEquals(2, twoIntegerClass.field2);

        twoIntegerClass = (TwoIntegerClass) jsonolizer.jsonToObj(json, TwoIntegerClass.class, predicate.negate());
        assertNull(twoIntegerClass);

        predicate = PredicateFactory.createPredicateLessThan("field1", 5);
        twoIntegerClass = (TwoIntegerClass) jsonolizer.jsonToObj(json, TwoIntegerClass.class, predicate);
        assertEquals(1, twoIntegerClass.field1);
        assertEquals(2, twoIntegerClass.field2);
        twoIntegerClass = (TwoIntegerClass) jsonolizer.jsonToObj(json, TwoIntegerClass.class, predicate.negate());
        assertNull(twoIntegerClass);
    }

    @Test
    public void testFilteringArrays() {
        String json = "[{\"field1\": 0, \"field2\": 0}, {\"field1\": 2, \"field2\": 2}, {\"field1\": 4, \"field2\": 4}]";
        Predicate<Map<String, String>> moreThan1Field1 = PredicateFactory.createPredicateMoreThan("field1", 1);
        Predicate<Map<String, String>> lessThan3Field2 = PredicateFactory.createPredicateLessThan("field2", 3);

        TwoIntegerClass[] arr = (TwoIntegerClass[]) jsonolizer.jsonToObj(json, TwoIntegerClass[].class);
        assertEquals(3, arr.length);

        arr = (TwoIntegerClass[]) jsonolizer.jsonToObj(json, TwoIntegerClass[].class, moreThan1Field1);
        System.out.println(Arrays.toString(arr));
        assertEquals(2, arr.length);
        arr = (TwoIntegerClass[]) jsonolizer.jsonToObj(json, TwoIntegerClass[].class, lessThan3Field2);
        assertEquals(2, arr.length);
        arr = (TwoIntegerClass[]) jsonolizer.jsonToObj(json, TwoIntegerClass[].class, moreThan1Field1.and(lessThan3Field2));
        assertEquals(1, arr.length);
    }

    @Test
    public void jsonToComplexObj() {
        String json = "{\"clazz\": {\"field\": 2}}";
        Class1 clazz = (Class1) jsonolizer.jsonToObj(json, Class1.class);
        assertEquals(2, clazz.clazz.field);
    }

    public static void main(String[] args) {
        Jsonolizer jsonolizer = new Jsonolizer();
        Class1 clazz1 = new Class1(new Class2(2));
        System.out.println(jsonolizer.objToJson(clazz1));

    }

}
