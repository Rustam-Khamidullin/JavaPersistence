package org.example;

import com.google.gson.Gson;

public class Main {
	public static void main(String[] args) {
		try {
			Gson gson = new Gson();

			// Создаем объект
			User user1 = new User();
			user1.age = 1;
			User user2 = new User();
			user2.age = 2;

			user1.user = user2;
			user2.user = null;

			String jsonString = gson.toJson(user1);
			System.out.println(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class User {
		public User user;
		public int age;
	}


}