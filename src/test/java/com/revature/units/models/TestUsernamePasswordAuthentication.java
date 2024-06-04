package com.revature.units.models;

import com.revature.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.revature.models.UsernamePasswordAuthentication;

public class TestUsernamePasswordAuthentication {
    @BeforeEach
    public void setup(){}
    @AfterEach
    public void teardown(){}

    @Test
    public void testEqualsAllField(){
        UsernamePasswordAuthentication uauth1 = new UsernamePasswordAuthentication();
        uauth1.setUsername("testUsername");
        uauth1.setPassword("testPassword");
        UsernamePasswordAuthentication uauth2 = new UsernamePasswordAuthentication();
        uauth2.setUsername("testUsername");
        uauth2.setPassword("testPassword");
        Assertions.assertEquals(uauth2, uauth1);
    }
    @Test
    public void testEqualsNull(){
        UsernamePasswordAuthentication uauth1 = new UsernamePasswordAuthentication();
        UsernamePasswordAuthentication uauth2 = null;
        Assertions.assertNotEquals(uauth2, uauth1);
        Assertions.assertFalse(uauth1.equals(uauth2));
    }
    @Test
    public void testEqualsSelf(){
        UsernamePasswordAuthentication uauth1 = new UsernamePasswordAuthentication();
        UsernamePasswordAuthentication uauth2 = uauth1;
        Assertions.assertEquals(uauth2, uauth1);
    }
    @Test
    public void testEqualsUsernameNull(){
        UsernamePasswordAuthentication uauth1 = new UsernamePasswordAuthentication();
        uauth1.setUsername("testUsername");
        UsernamePasswordAuthentication uauth2 = new UsernamePasswordAuthentication();
        Assertions.assertNotEquals(uauth2, uauth1);
    }
    @Test
    public void testNotEqualsUsername(){
        UsernamePasswordAuthentication uauth1 = new UsernamePasswordAuthentication();
        uauth1.setUsername("testUsername");
        UsernamePasswordAuthentication uauth2 = new UsernamePasswordAuthentication();
        uauth2.setUsername("testUsername1");
        Assertions.assertNotEquals(uauth2, uauth1);
    }
    @Test
    public void testEqualsPasswordNull(){
        UsernamePasswordAuthentication uauth1 = new UsernamePasswordAuthentication();
        uauth1.setUsername("testUsername");
        uauth1.setPassword("validPassword");
        UsernamePasswordAuthentication uauth2 = new UsernamePasswordAuthentication();
        uauth2.setUsername("testUsername");
        Assertions.assertNotEquals(uauth2, uauth1);


    }
    @Test
    public void testEqualsPasswordNotEqual() {
        UsernamePasswordAuthentication uauth1 = new UsernamePasswordAuthentication();
        uauth1.setUsername("testUsername");
        uauth1.setPassword("testPassword");
        UsernamePasswordAuthentication uauth2 = new UsernamePasswordAuthentication();
        uauth2.setUsername("testUsername");
        uauth2.setPassword("testPassword1");
        Assertions.assertNotEquals(uauth2, uauth1);

    }
    @Test
    public void testEqualsNotClass() {
        UsernamePasswordAuthentication uauth1 = new UsernamePasswordAuthentication();
        uauth1.setUsername("testUsername");
        uauth1.setPassword("testPassword");
        User uauth2 = new User();
        uauth2.setUsername("testUsername");
        uauth2.setPassword("testPassword");
        Assertions.assertNotEquals(uauth2, uauth1);
        Assertions.assertFalse(uauth1.equals(uauth2));
    }
    @Test
    public void testToString(){
        UsernamePasswordAuthentication uauth1 = new UsernamePasswordAuthentication();
        uauth1.setUsername("testUsername");
        uauth1.setPassword("testPassword");
        Assertions.assertEquals("UsernamePasswordAuthentication [username=testUsername]", uauth1.toString());
    }
}
