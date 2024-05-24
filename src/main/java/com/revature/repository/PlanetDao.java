package com.revature.repository;

import java.util.ArrayList;
import java.util.List;

import com.revature.exceptions.PlanetFailException;
import com.revature.models.Planet;
import com.revature.utilities.ConnectionUtil;

import java.sql.*;

public class PlanetDao {

    public List<Planet> getAllPlanets(int ownerId) {
        // TODO: implement
        List<Planet> planets = new ArrayList<>();
        try (Connection connection = ConnectionUtil.createConnection()) {
            String sql = "SELECT id, name, ownerId FROM planets WHERE ownerId = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int owner_Id = rs.getInt("ownerId");

                Planet planet = new Planet();
                planet.setId(id);
                planet.setName(name);
                planet.setOwnerId(owner_Id);

                planets.add(planet);
            }
        } catch (SQLException e) {
            throw new PlanetFailException("Error retrieving planets");
        }
        return planets;
    }

    public Planet getPlanetByName(int ownerId, String planetName) {
        // TODO: implement
        try (Connection connection = ConnectionUtil.createConnection()) {
//            System.out.println("PlanetDAO getPlanetByName: " + planetName + " and ID: " + ownerId);
            String sql = "SELECT id, name, ownerId FROM planets WHERE name = ? AND ownerId = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, planetName);
            ps.setInt(2, ownerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int owner_Id = rs.getInt("ownerId");

                Planet planet = new Planet();
                planet.setId(id);
                planet.setName(name);
                planet.setOwnerId(owner_Id);

//                System.out.println("PlanetDAO: " + name + " and ID: " + owner_Id);
                return planet;
            }
        } catch (SQLException e) {
            throw new PlanetFailException("Error retrieving planets");
        }
        return null;
    }

    public Planet getPlanetById(int ownerId, int planetId) {
        try (Connection connection = ConnectionUtil.createConnection()) {
            String sql = "SELECT id, name, ownerId FROM planets WHERE id = ? AND ownerId = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, planetId);
            ps.setInt(2, ownerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Existing planet found with the specified ID and owner ID
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int owner_Id = rs.getInt("ownerId");

                Planet planet = new Planet();
                planet.setId(id);
                planet.setName(name);
                planet.setOwnerId(owner_Id);

                return planet;
            } else {
                // No planet found with the specified ID and owner ID
//                System.out.println("Planet not found with ID: " + planetId + " and owner ID: " + ownerId);
                return null;
            }
        } catch (SQLException e) {
            throw new PlanetFailException("Error retrieving planets");
        }
    }

    public Planet createPlanet(Planet p) {
        // TODO: implement
        try (Connection connection = ConnectionUtil.createConnection()) {
            String sql = "INSERT INTO planets (name, ownerId) VALUES (?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, p.getName());
            ps.setInt(2, p.getOwnerId());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1); // Retrieve the auto-generated id
                p.setId(generatedId); // Set the id to the planet object
                return p;
            }
            return null; // Failed to create planet
        } catch (SQLException e) {
            throw new PlanetFailException("Error creating planets");
        }
    }

    public boolean deletePlanetById(int ownerId, int planetId) {
        // TODO: implement
        try (Connection connection = ConnectionUtil.createConnection()) {
            // Check if the planet exists for the given ownerId and planetId
            Planet existingPlanet = getPlanetById(ownerId, planetId);
//            System.out.println("exisiting planet " + existingPlanet);
            if (existingPlanet != null) {
                // Construct SQL query to delete associated moons if present
                String deleteMoonsSql = "DELETE FROM moons WHERE myPlanetId = ?";
                PreparedStatement deleteMoonsPs = connection.prepareStatement(deleteMoonsSql);
                deleteMoonsPs.setInt(1, planetId);

                // Execute delete statement for associated moons
                int moonsRowsAffected = deleteMoonsPs.executeUpdate();

                // Construct SQL query to delete the planet
                String deletePlanetSql = "DELETE FROM planets WHERE id = ? AND ownerId = ?";
                PreparedStatement deletePlanetPs = connection.prepareStatement(deletePlanetSql);
                deletePlanetPs.setInt(1, planetId);
                deletePlanetPs.setInt(2, ownerId);

                // Execute delete statement for the planet
                int planetRowsAffected = deletePlanetPs.executeUpdate();

                // Check if the planet or any associated moons were deleted
                if (planetRowsAffected > 0 || moonsRowsAffected > 0) {
                    return true;
                } else {
                    System.out.println("No planet or associated moons were deleted for planet ID " + planetId + " and owner ID " + ownerId);
                    return false;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new PlanetFailException("Error deleting planet");
        }
    }
}
