package com.revature.utilities;

import com.revature.MainDriver;
import com.revature.models.Moon;
import com.revature.models.Planet;
import com.revature.models.User;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class ConnectionUtil {

    public static Connection createConnection() throws SQLException {
        InputStream props = MainDriver.class.getClassLoader().getResourceAsStream("database.properties");
        Properties properties = new Properties();
        try {
            properties.load(props);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        boolean useTest = Boolean.parseBoolean(properties.getProperty("test-mode"));
        if (useTest) {
            return DriverManager.getConnection("jdbc:sqlite:src/test/resources/PlanetariumForTest.db");
        }
        return DriverManager.getConnection("jdbc:sqlite:src/main/resources/planetarium.db");
    }
    public static void deleteTables() throws SQLException{
        try(Connection connection = ConnectionUtil.createConnection();){
            List<String> tables = new ArrayList<>();
            tables.add("users");
            tables.add("planets");
            tables.add("moons");
            for(String t: tables){
                String sql = String.format("DELETE FROM %s", t);
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.executeUpdate();
            }
            System.out.println("Database table rows are deleted.");
        } catch (Exception e){
            System.err.println("Database table rows cannot be deleted. "+ e.getMessage());
        }
    }
    public static Map<String, Object> dataSeeding() throws SQLException{
        Map<String, Object> result = new HashMap<>();
        User user = new User();
        user.setUsername("userseed");
        user.setPassword("valid");
        Planet planet = new Planet();
        planet.setName("earth");
        Moon moon = new Moon();
        moon.setName("redmoon");
        try(Connection connection = ConnectionUtil.createConnection();){
            try {
                String sql = "INSERT INTO Users (username, password) VALUES (?, ?)";
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                    result.put("user", user);
                    planet.setOwnerId(user.getId());
                }
            } catch (NullPointerException e){
                System.out.println("Null object referenced. "+e.getMessage());
            }
            try {
                String sql = "INSERT INTO Planets (name, ownerId) VALUES (?, ?)";
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, planet.getName());
                ps.setInt(2, user.getId());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()){
                    planet.setId(rs.getInt(1));
                    result.put("planet", planet);
                    moon.setMyPlanetId(planet.getId());
                }
            } catch (NullPointerException e){
                System.out.println("Null object referenced. "+e.getMessage());
            }
            try {
                String sql = "INSERT INTO Moons (name, myPlanetId) VALUES (?, ?)";
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, moon.getName());
                ps.setInt(2, planet.getId());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()){
                    moon.setId(rs.getInt(1));
                    result.put("moon", moon);
                }
            } catch (NullPointerException e){
                System.out.println("Null object referenced. "+e.getMessage());
            }

            System.out.println("Database seeds the data successfully. "+ result);
            return result;
        } catch (Exception e){
            System.err.println("Database cannot seed the data. "+ e.getMessage());
        }
        return null;
    }

}
