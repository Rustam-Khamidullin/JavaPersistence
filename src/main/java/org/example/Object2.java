package org.example;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class)
public class Object2 {
    private Object1 object1;

    // Геттеры и сеттеры
    public Object1 getObject1() {
        return object1;
    }

    public void setObject1(Object1 object1) {
        this.object1 = object1;
    }
}