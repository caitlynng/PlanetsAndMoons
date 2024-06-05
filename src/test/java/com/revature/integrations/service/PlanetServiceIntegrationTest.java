package com.revature.integrations.service;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import com.revature.models.Planet;
import com.revature.repository.PlanetDao;
import com.revature.service.PlanetService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static com.revature.selenium.utils.DriverUtils.*;

@TestMethodOrder(OrderAnnotation.class)
public class PlanetServiceIntegrationTest {

    private PlanetDao planetDao;
    private PlanetService planetService;
    private Planet testPlanet;

    @BeforeEach
    public void setUp() throws SQLException {
        cleanDatabaseTable();

        planetDao = new PlanetDao();
        planetService = new PlanetService(planetDao);

        // Arrange
        testPlanet = new Planet();
        testPlanet.setName("test planet");
        testPlanet.setOwnerId(1);
        testPlanet = planetDao.createPlanet(testPlanet);

//        System.out.println(testPlanet);
    }


    @Test
    @DisplayName("CreatePlanet::Positive")
    @Order(1)
    public void testCreatePlanetIntegration() throws SQLException {
        // Arrange
        Planet newPlanet = new Planet();
        newPlanet.setName("new planet");
        newPlanet.setOwnerId(1);

        // Act
        Planet createdPlanet = planetService.createPlanet(1, newPlanet);

        // Assert
        assertNotNull(createdPlanet);
        assertEquals(newPlanet.getName(), createdPlanet.getName());
        assertEquals(newPlanet.getOwnerId(), createdPlanet.getOwnerId());
    }

    @Test
    @DisplayName("GetPlanetByName::Positive")
    @Order(2)
    public void testGetPlanetByNameIntegration() throws SQLException {
        // Act
        Planet retrievedPlanet = planetService.getPlanetByName(testPlanet.getOwnerId(), testPlanet.getName());

        // Assert
        assertNotNull(retrievedPlanet);
        assertEquals(testPlanet.getName(), retrievedPlanet.getName());
        assertEquals(testPlanet.getOwnerId(), retrievedPlanet.getOwnerId());
    }

    @Test
    @DisplayName("GetPlanetById::Positive")
    @Order(3)
    public void testGetPlanetByIdIntegration() throws SQLException {
        // Act
        Planet retrievedPlanet = planetService.getPlanetById(testPlanet.getOwnerId(), testPlanet.getId());

        // Assert
        assertNotNull(retrievedPlanet);
        assertEquals(testPlanet.getId(), retrievedPlanet.getId());
        assertEquals(testPlanet.getName(), retrievedPlanet.getName());
        assertEquals(testPlanet.getOwnerId(), retrievedPlanet.getOwnerId());
    }

    @Test
    @DisplayName("DeletePlanetById::Positive")
    @Order(4)
    public void testDeletePlanetByIdIntegration() throws SQLException {
        // Act
        boolean isDeleted = planetService.deletePlanetById(testPlanet.getOwnerId(), testPlanet.getId());

        // Assert
        assertTrue(isDeleted);

        // Verify the planet is actually deleted
        Planet deletedPlanet = planetService.getPlanetById(testPlanet.getOwnerId(), testPlanet.getId());
        assertNull(deletedPlanet);
    }

    @Test
    @DisplayName("GetAllPlanets::Positive")
    @Order(5)
    public void testGetAllPlanetsIntegration() throws SQLException {
        // Arrange
        Planet planet1 = new Planet();
        planet1.setName("planet1");
        planet1.setOwnerId(testPlanet.getOwnerId());
        planetService.createPlanet(testPlanet.getOwnerId(), planet1);

        Planet planet2 = new Planet();
        planet2.setName("planet2");
        planet2.setOwnerId(testPlanet.getOwnerId());
        planetService.createPlanet(testPlanet.getOwnerId(), planet2);

        // Act
        List<Planet> retrievedPlanets = planetService.getAllPlanets(testPlanet.getOwnerId());

        // Assert
        assertNotNull(retrievedPlanets);
        assertEquals(3, retrievedPlanets.size()); // Including the initial testPlanet
    }

    @Test
    @DisplayName("GetPlanetByName::Negative - Planet Not Found")
    @Order(6)
    public void testGetPlanetByNameNotFoundIntegration() {
        // Arrange
        String nonExistentName = "NonExistentPlanet";

        // Act
        Planet retrievedPlanet = planetService.getPlanetByName(testPlanet.getOwnerId(), nonExistentName);

        // Assert
        assertNull(retrievedPlanet);
    }

    @Test
    @DisplayName("GetPlanetById::Negative - Planet Not Found")
    @Order(7)
    public void testGetPlanetByIdNotFoundIntegration() {
        // Arrange
        int nonExistentId = 999;

        // Act
        Planet retrievedPlanet = planetService.getPlanetById(testPlanet.getOwnerId(), nonExistentId);

        // Assert
        assertNull(retrievedPlanet);
    }

    @Test
    @DisplayName("DeletePlanetById::Negative - Planet Not Found")
    @Order(8)
    public void testDeletePlanetByIdNotFoundIntegration() {
        // Arrange
        int nonExistentId = 999;

        // Act
        boolean isDeleted = planetService.deletePlanetById(testPlanet.getOwnerId(), nonExistentId);

        // Assert
        assertFalse(isDeleted);
    }
}
