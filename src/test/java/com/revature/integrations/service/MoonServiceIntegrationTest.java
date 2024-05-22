package com.revature.integrations.service;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;


import com.revature.models.Planet;
import com.revature.repository.PlanetDao;
import com.revature.service.MoonService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import com.revature.models.Moon;
import com.revature.repository.MoonDao;

import static com.revature.selenium.utils.DriverUtils.*;

@TestMethodOrder(OrderAnnotation.class)
public class MoonServiceIntegrationTest {

    private MoonDao moonDao;
    private MoonService moonService;
    private PlanetDao planetDao;
    private Planet testPlanet;
    private Moon testMoon;


    @BeforeEach
    public void setUp() throws SQLException {
        cleanDatabaseTable();

        moonDao = new MoonDao();
        moonService = new MoonService(moonDao);

        // Arrange
        testPlanet = new Planet();
        planetDao = new PlanetDao();
        testPlanet.setName("test planet");
        testPlanet.setOwnerId(1);
        testPlanet = planetDao.createPlanet(testPlanet);

        System.out.println(testPlanet);
    }

    @Test
    @DisplayName("CreateMoon::Positive ")
    @Order(1)
    public void testCreateMoonIntegration() throws SQLException {
        String moonName = "testmoon";
        testMoon = new Moon();
        testMoon.setName(moonName);
        testMoon.setMyPlanetId(testPlanet.getId());

        Moon expectedMoon = moonService.createMoon(testMoon);

        // Assert
        assertNotNull(expectedMoon);
        assertEquals(testMoon.getName(), expectedMoon.getName());
        assertEquals(testMoon.getMyPlanetId(), expectedMoon.getMyPlanetId());
    }

    @Test
    @DisplayName("GetMoonByName::Positive ")
    @Order(1)
    public void testGetMoonByNameIntegration() throws SQLException {
        String moonName = "testmoon";
        testMoon = new Moon();
        testMoon.setName(moonName);
        testMoon.setMyPlanetId(testPlanet.getId());
        moonService.createMoon(testMoon);

        // Act
        Moon expectedMoon = moonService.getMoonByName(testPlanet.getId(), testMoon.getName());
        System.out.println("testMoon: " + testMoon);
        System.out.println("expMoon: " + expectedMoon);

        // Assert
        assertNotNull(expectedMoon);
        assertEquals(testMoon.getName(), expectedMoon.getName());
        assertEquals(testMoon.getMyPlanetId(), expectedMoon.getMyPlanetId());
    }

    @Test
    @DisplayName("GetMoonById::Positive")
    @Order(2)
    public void testGetMoonByIdIntegration() throws SQLException {
        // Arrange
        String moonName = "testmoon";
        testMoon = new Moon();
        testMoon.setName(moonName);
        testMoon.setMyPlanetId(testPlanet.getId());
        moonService.createMoon(testMoon);

        //Act
        Moon expectedMoon = moonService.getMoonById(testPlanet.getId(), testMoon.getId());

        // Assert
        assertNotNull(expectedMoon);
        assertEquals(testMoon.getId(), expectedMoon.getId());
        assertEquals(testMoon.getName(), expectedMoon.getName());
        assertEquals(testMoon.getMyPlanetId(), expectedMoon.getMyPlanetId());
    }

    @Test
    @DisplayName("DeleteMoonById::Positive")
    @Order(3)
    public void testDeleteMoonByIdIntegration() throws SQLException {
        // Arrange
        String moonName = "testmoon";
        testMoon = new Moon();
        testMoon.setName(moonName);
        testMoon.setMyPlanetId(testPlanet.getId());
        moonService.createMoon(testMoon);

        boolean isDeleted = moonService.deleteMoonById(testMoon.getId());

        // Assert
        assertTrue(isDeleted);

        // Verify the moon is actually deleted
        Moon deletedMoon = moonService.getMoonById(testPlanet.getId(), testMoon.getId());
        assertNull(deletedMoon);
    }

    @Test
    @DisplayName("GetMoonsFromPlanet::Positive")
    @Order(4)
    public void testGetMoonsFromPlanetIntegration() throws SQLException {
        // Arrange
        Moon moon1 = new Moon();
        moon1.setName("Moon1");
        moon1.setMyPlanetId(testPlanet.getId());
        moonService.createMoon(moon1);

        Moon moon2 = new Moon();
        moon2.setName("Moon2");
        moon2.setMyPlanetId(testPlanet.getId());
        moonService.createMoon(moon2);

        // Act
        List<Moon> expectedMoon = moonService.getMoonsFromPlanet(testPlanet.getId());

        // Assert
        assertNotNull(expectedMoon);
        assertEquals(2, expectedMoon.size());
        assertEquals(moon1.getName(), expectedMoon.get(0).getName());
        assertEquals(moon1.getMyPlanetId(), expectedMoon.get(0).getMyPlanetId());
    }

    @Test
    @DisplayName("GetMoonByName::Negative - Moon Not Found")
    @Order(5)
    public void testGetMoonByNameNotFoundIntegration() {
        // Arrange
        String nonExistentName = "NonExistentMoon";

        // Act
        Moon expectedMoon = moonService.getMoonByName(1, nonExistentName);

        // Assert
        assertNull(expectedMoon);
    }

    @Test
    @DisplayName(" GetMoonById::Negative - Moon Not Found")
    @Order(6)
    public void testGetMoonByIdNotFoundIntegration() {
        // Arrange
        int nonExistentId = 999;

        // Act
        Moon expectedMoon = moonService.getMoonById(1, nonExistentId);

        // Assert
        assertNull(expectedMoon);
    }

    @Test
    @DisplayName("DeleteMoonById::Negative - Moon Not Found")
    @Order(7)
    public void testDeleteMoonByIdNotFoundIntegration() {
        // Arrange
        int nonExistentId = 999;

        // Act
        boolean isDeleted = moonService.deleteMoonById(nonExistentId);

        // Assert
        assertFalse(isDeleted);
    }

    @Test
    @DisplayName("GetMoonsFromPlanet::Negative - Moon Not Found")
    @Order(8)
    public void testGetMoonsFromPlanetNotFoundIntegration() {
        // Arrange
        int nonExistentPlanetId = 999;

        // Act
        List<Moon> expectedMoon = moonService.getMoonsFromPlanet(nonExistentPlanetId);

        // Assert
        assertNotNull(expectedMoon);
        assertTrue(expectedMoon.isEmpty());
    }
}

