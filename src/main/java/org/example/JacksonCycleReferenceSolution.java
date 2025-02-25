package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonCycleReferenceSolution {
    public static void main(String[] args) throws Exception {
        Object1 object1 = new Object1();
        Object2 object2 = new Object2();

        object1.setObject2(object2);
        object2.setObject1(object1);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(object1);
        System.out.println(json);
    }
}