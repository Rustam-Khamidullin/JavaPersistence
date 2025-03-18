package org.example;

import java.util.Arrays;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        Simple s = new Simple();
        s.string = new String[] {"string", "string2"};
        s.value = new int[] {43};

        Simple example = new Simple();
        example.string = new String[] {"str"};
        example.value = new int[] {42, 43};
        example.simple = s;

        Jsonolizer jsonlizer = new Jsonolizer();
        System.out.println(jsonlizer.objToJson(example));
        System.out.println(jsonlizer.objToJson(s));
        System.out.println(jsonlizer.objToJson(new Obj(15)));
        System.out.println(jsonlizer.jsonToObj("{\"str\": \"st\\\"rin\"g\"}", Str.class));
        System.out.println(jsonlizer.objToJson(new int[] {1,2,3}));
        System.out.println(Arrays.toString((int[])jsonlizer.jsonToObj("[1, 2, 3]", int[].class)));
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