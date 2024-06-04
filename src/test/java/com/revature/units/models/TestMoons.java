package com.revature.units.models;

import com.revature.models.Moon;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestMoons {
    @Test
    public void testNotEqualsNull() {
        Moon moon1 = new Moon();
        Moon moon2 = null;
        Assertions.assertFalse(moon1.equals(moon2));
        Assertions.assertNotEquals(moon1, moon2);
    }

    @Test
    public void testEqualsSelf() {
        Moon moon1 = new Moon();
        Moon moon2 = moon1;
        Assertions.assertEquals(moon1, moon2);
        Assertions.assertTrue(moon1.equals(moon2));
    }

    @Test
    public void testNotEqualsId() {
        Moon moon1 = new Moon();
        moon1.setId(10);
        Moon moon2 = new Moon();
        moon2.setId(200);
        Assertions.assertNotEquals(moon1, moon2);
        Assertions.assertFalse(moon1.equals(moon2));
    }
}
