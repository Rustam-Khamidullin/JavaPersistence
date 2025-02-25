package org.example;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class)
public class Object1 {
    private Object2 object2;

    // Геттеры и сеттеры
    public Object2 getObject2() {
        return object2;
    }

    public void setObject2(Object2 object2) {
        this.object2 = object2;
    }
}