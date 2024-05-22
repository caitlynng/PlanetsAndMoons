package com.revature.integrations.repository;

import com.revature.models.Moon;
import com.revature.repository.MoonDao;
import com.revature.utilities.ConnectionUtil;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MoonDaoTest {
    private Connection connection;
    private MoonDao moonDao;

    @BeforeEach
    public void setUp() throws SQLException {
        connection = ConnectionUtil.createConnection();
        moonDao = new MoonDao();
        cleanupMoonsTable();
        cleanupPlanetsTable();
    }

    @DisplayName("CreateMoon::Valid")
    @Order(1)
    @Test
    public void createMoonValid() throws SQLException {
        // Arrange
        int planetId = insertTestPlanet("Earth");
        Moon moon = new Moon();
        moon.setName("Moon");
        moon.setMyPlanetId(planetId);

        // Act
        Moon createdMoon = moonDao.createMoon(moon);

        // Assert
        Assertions.assertNotNull(createdMoon);
        Assertions.assertEquals("Moon", createdMoon.getName());
        Assertions.assertEquals(planetId, createdMoon.getMyPlanetId());
        Assertions.assertTrue(createdMoon.getId() > 0);
    }

    @DisplayName("CreateMoon::Invalid__NonExistingPlanet")
    @Order(2)
    @Test
    public void createMoonInvalid() {
        // Arrange
        Moon moon = new Moon();
        moon.setName("Moon");
        moon.setMyPlanetId(999); // Non-existing planet ID

        // Act
        Moon createdMoon = moonDao.createMoon(moon);

        // Assert
        Assertions.assertNull(createdMoon);
    }

    @DisplayName("GetMoonByName::Valid")
    @Order(3)
    @Test
    public void getMoonByNameValid() throws SQLException {
        // Arrange
        int planetId = insertTestPlanet("Mars");
        insertTestMoon(planetId, "Phobos");

        // Act
        Moon foundMoon = moonDao.getMoonByName("Phobos");

        // Assert
        Assertions.assertNotNull(foundMoon);
        Assertions.assertEquals("Phobos", foundMoon.getName());
        Assertions.assertEquals(planetId, foundMoon.getMyPlanetId());
    }

    @DisplayName("GetMoonByName::Invalid__NonExistingMoon")
    @Order(4)
    @Test
    public void getMoonByNameInvalid() {
        // Arrange
        String nonExistingMoonName = "Deimos";

        // Act
        Moon nonExistingMoon = moonDao.getMoonByName(nonExistingMoonName);

        // Assert
        Assertions.assertNull(nonExistingMoon);
    }

    @DisplayName("DeleteMoonById::Valid")
    @Order(5)
    @Test
    public void deleteMoonByIdValid() throws SQLException {
        // Arrange
        int planetId = insertTestPlanet("Jupiter");
        int moonId = insertTestMoon(planetId, "Ganymede");

        // Act
        boolean deletionResult = moonDao.deleteMoonById(moonId);

        // Assert
        Assertions.assertTrue(deletionResult);
    }

    @DisplayName("DeleteMoonById::Invalid__NonExistingMoon")
    @Order(6)
    @Test
    public void deleteMoonByIdInvalid() {
        // Arrange
        int nonExistingMoonId = 999;

        // Act
        boolean deletionResult = moonDao.deleteMoonById(nonExistingMoonId);

        // Assert
        Assertions.assertFalse(deletionResult);
    }

    @DisplayName("GetAllMoons::Valid")
    @Order(7)
    @Test
    public void getAllMoonsValid() throws SQLException {
        // Arrange
        int ownerId = 1;
        int planetId1 = insertTestPlanet("Earth");
        int planetId2 = insertTestPlanet("Mars");
        insertTestMoon(planetId1, "Moon");
        insertTestMoon(planetId2, "Phobos");

        // Act
        List<Moon> moons = moonDao.getAllMoons(ownerId);

        // Assert
        Assertions.assertNotNull(moons);
        Assertions.assertEquals(2, moons.size());
        Assertions.assertTrue(moons.stream().anyMatch(moon -> "Moon".equals(moon.getName())));
        Assertions.assertTrue(moons.stream().anyMatch(moon -> "Phobos".equals(moon.getName())));
    }


    @DisplayName("GetMoonsFromPlanet::Valid")
    @Order(8)
    @Test
    public void getMoonsFromPlanetValid() throws SQLException {
        // Arrange
        int planetId = insertTestPlanet("Earth");
        insertTestMoon(planetId, "Moon");
        insertTestMoon(planetId, "Deimos");

        // Act
        List<Moon> moons = moonDao.getMoonsFromPlanet(planetId);

        // Assert
        Assertions.assertNotNull(moons);
        Assertions.assertEquals(2, moons.size());
        Assertions.assertTrue(moons.stream().anyMatch(moon -> "Moon".equals(moon.getName())));
        Assertions.assertTrue(moons.stream().anyMatch(moon -> "Deimos".equals(moon.getName())));
    }

    private int insertTestPlanet(String planetName) throws SQLException {
        String insertQuery = "INSERT INTO planets (name, ownerId) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, planetName);
            statement.setInt(2, 1); // Assuming owner ID 1
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Return the generated planet ID
            }
        }
        return -1; // Failed to insert planet
    }

    private int insertTestMoon(int planetId, String moonName) throws SQLException {
        String insertQuery = "INSERT INTO moons (name, myPlanetId) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, moonName);
            statement.setInt(2, planetId);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Return the generated moon ID
            }
        }
        return -1; // Failed to insert moon
    }

    private void cleanupMoonsTable() throws SQLException {
        String deleteQuery = "DELETE FROM moons";
        try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.executeUpdate();
        }
    }

    private void cleanupPlanetsTable() throws SQLException {
        String deleteQuery = "DELETE FROM planets";
        try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.executeUpdate();
        }
    }
}