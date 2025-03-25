package org.example;

import org.example.filter_conditions.PredicateFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class Main {
	public static void main(String[] args) {
		Test test = new Test(3, 3);
		JsonParser parser = new JsonParser();
		Jsonolizer jsonolizer = new Jsonolizer();
		System.out.println(jsonolizer.objToJson(test));
		String json = jsonolizer.objToJson(test);
		jsonolizer.jsonToObj(json, Test.class);

		// Пример карты с данными
		Map<String, String> keyValues = new HashMap<>();
		keyValues.put("field1", "10");
		keyValues.put("field2", "3");

		//Создаем предикат, который проверяет, больше ли значение поля "field1" чем 5
		Predicate<Map<String, String>> predicate = PredicateFactory.createPredicateMoreThan("field1", 5);
		Predicate<Map<String, String>> predicate2 = PredicateFactory.createPredicateMoreThan("field2", 5);


		// Проверяем предикат
		boolean result = predicate.test(keyValues);
		System.out.println("Is field1 greater than 5? " + result); // true

		System.out.println(jsonolizer.jsonToComplexObject(json, Test.class, predicate.and(predicate2)));
	}

	@Identity(field = "identity")
	public static class Test {
		public Test() {

		}

		public int field1;
		public int field2;

		public Test(int field1, int field2) {
			this.field1 = field1;
			this.field2 = field2;
		}
	}

	public static class User {
		public User user;
		public int age;
	}

	public static class Obj {
		public Obj(Integer age) {
			this.age = age;
		}

		public Integer age;

		@Override
		public String toString() {
			return String.valueOf(age);
		}
	}

	public static class Str {
		public Str(String str) {
			this.str = str;
		}

		public String str;

		@Override
		public String toString() {
			return String.valueOf(str);
		}
	}


	public static class Simple {
		public int[] value;
		public String[] string;
		public Simple simple;
	}
}