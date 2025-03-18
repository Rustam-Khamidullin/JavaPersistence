package org.example;

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
        System.out.println(jsonlizer.objToJson(new Str("alskdjladj\"asdal\"kjsdlk")));
    }

    public static class User {
        public User user;
        public int age;
    }
    public static class Str {
        public Str(String str) {
            this.str = str;
        }
        public String str;
    }


    public static class Simple {
        public int[] value;
        public String[] string;
        public Simple simple;
    }
}