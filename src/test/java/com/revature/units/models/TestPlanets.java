package com.revature.units.models;

import com.revature.models.Moon;
import com.revature.models.Planet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestPlanets {
    @Test
    public void testEqualsAllFields(){
        Planet planet1 = new Planet();
        planet1.setId(20);
        planet1.setName("planet");
        planet1.setOwnerId(10);
        Planet planet2 = new Planet();
        planet2.setName("planet");
        planet2.setOwnerId(10);
        planet2.setId(20);
        Assertions.assertEquals(planet1, planet2);
    }

    @Test
    public void testNotEqualsNull(){
        Planet planet1 = null;
        Planet planet2 = new Planet();
        planet2.setName("planet");
        planet2.setOwnerId(10);
        planet2.setId(20);
        Assertions.assertNotEquals(planet2, planet1);
        Assertions.assertFalse(planet2.equals(planet1));
    }
    @Test
    public void testEqualsSelf(){
        Planet planet2 = new Planet();
        Planet planet1 = planet2;
        planet2.setName("planet");
        planet2.setOwnerId(10);
        planet2.setId(20);
        Assertions.assertEquals(planet1, planet2);

    }
    @Test
    public void testNotEqualsClass(){
        Planet planet2 = new Planet();
        Moon planet1 = new Moon();
        planet2.setName("planet");
        planet2.setOwnerId(10);
        planet2.setId(20);
        Assertions.assertNotEquals(planet2, planet1);
        Assertions.assertFalse(planet2.equals(planet1));
    }
    @Test
    public void testNotEqualsId(){
        Planet planet1 = new Planet();
        planet1.setName("planet");
        planet1.setOwnerId(10);
        planet1.setId(200);
        Planet planet2 = new Planet();
        planet2.setName("planet");
        planet2.setOwnerId(10);
        planet2.setId(20);
        Assertions.assertNotEquals(planet1, planet2);
    }
}
