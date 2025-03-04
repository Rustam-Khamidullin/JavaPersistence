package org.example;

public class Main {
    public static void main(String[] args) {
        Simple example = new Simple();
        example.string = "str";
        example.value = 42;

        Jsonolizer jsonlizer = new Jsonolizer();
        System.out.println(jsonlizer.objToJson(example));
    }

    public static class User {
        public User user;
        public int age;
    }


    public static class Simple {
        public int value;
        public String string;
    }
}