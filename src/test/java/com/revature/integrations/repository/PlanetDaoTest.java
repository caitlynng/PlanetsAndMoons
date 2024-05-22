package com.revature.integrations.repository;

import com.revature.models.Planet;
import com.revature.repository.PlanetDao;
import com.revature.utilities.ConnectionUtil;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class PlanetDaoTest {
    private Connection connection;
    private PlanetDao planetDao;

    @BeforeEach
    public void setUp() throws SQLException {
        connection = ConnectionUtil.createConnection();
        planetDao = new PlanetDao();
        cleanupPlanetsTable();
    }

    @DisplayName("CreatePlanet::Valid")
    @Order(1)
    @Test
    public void createPlanetValid() {
        // Arrange
        Planet planet = new Planet();
        planet.setName("Earth");
        planet.setOwnerId(1);

        // Act
        Planet createdPlanet = planetDao.createPlanet(planet);

        // Assert
        Assertions.assertNotNull(createdPlanet);
        Assertions.assertEquals("Earth", createdPlanet.getName());
        Assertions.assertEquals(1, createdPlanet.getOwnerId());
        Assertions.assertTrue(createdPlanet.getId() > 0);
    }

    @DisplayName("GetPlanetByName::Valid")
    @Order(2)
    @Test
    public void getPlanetByNameValid() throws SQLException {
        // Arrange
        String planetName = "Mars";
        int ownerId = 1;
        insertTestPlanet(planetName, ownerId);

        // Act
        Planet foundPlanet = planetDao.getPlanetByName(ownerId, planetName);

        // Assert
        Assertions.assertNotNull(foundPlanet);
        Assertions.assertEquals(planetName, foundPlanet.getName());
        Assertions.assertEquals(ownerId, foundPlanet.getOwnerId());
    }

    @DisplayName("GetPlanetByName::Invalid__NonExistingPlanet")
    @Order(3)
    @Test
    public void getPlanetByNameInvalid() {
        // Arrange
        String nonExistingPlanetName = "Venus";
        int ownerId = 1;

        // Act
        Planet nonExistingPlanet = planetDao.getPlanetByName(ownerId, nonExistingPlanetName);

        // Assert
        Assertions.assertNull(nonExistingPlanet);
    }

    @DisplayName("DeletePlanetById::Valid")
    @Order(4)
    @Test
    public void deletePlanetByIdValid() throws SQLException {
        // Arrange
        String planetName = "Jupiter";
        int ownerId = 1;
        insertTestPlanet(planetName, ownerId);
        Planet createdPlanet = planetDao.getPlanetByName(ownerId, planetName);
        int createdPlanetId = createdPlanet.getId();

        // Act
        boolean deletionResult = planetDao.deletePlanetById(ownerId, createdPlanetId);

        // Assert
        Assertions.assertTrue(deletionResult);
    }

    @DisplayName("DeletePlanetById::Invalid__NonExistingPlanet")
    @Order(5)
    @Test
    public void deletePlanetByIdInvalid() {
        // Arrange
        int nonExistingPlanetId = 100;
        int ownerId = 1;

        // Act
        boolean deletionResult = planetDao.deletePlanetById(ownerId, nonExistingPlanetId);

        // Assert
        Assertions.assertFalse(deletionResult);
    }

    @DisplayName("GetAllPlanets::Valid")
    @Order(6)
    @Test
    public void getAllPlanetsValid() throws SQLException {
        // Arrange
        int ownerId1 = 1;
        int ownerId2 = 2;
        insertTestPlanet("earth", ownerId1);
        insertTestPlanet("mars", ownerId1);
        insertTestPlanet("jupiter", ownerId2); // Planet with a different ownerId

        // Act
        List<Planet> planets1 = planetDao.getAllPlanets(ownerId1);

        // Assert
        Assertions.assertNotNull(planets1);
        Assertions.assertEquals(2, planets1.size());
        Planet planet1 = planets1.get(0);
        Assertions.assertEquals("earth", planet1.getName());
        Assertions.assertEquals(ownerId1, planet1.getOwnerId());

        Planet planet2 = planets1.get(1);
        Assertions.assertEquals("mars", planet2.getName());
        Assertions.assertEquals(ownerId1, planet2.getOwnerId());

        // Act
        List<Planet> planets2 = planetDao.getAllPlanets(ownerId2);
        // Assert
        Assertions.assertNotNull(planets2);
        Assertions.assertEquals(1, planets2.size());
        Planet planet3 = planets2.get(0);
        Assertions.assertEquals("jupiter", planet3.getName());
        Assertions.assertEquals(ownerId2, planet3.getOwnerId());
    }

    private void insertTestPlanet(String planetName, int ownerId) throws SQLException {
        String insertQuery = "INSERT INTO planets (name, ownerId) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, planetName);
            statement.setInt(2, ownerId);
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
