package com.revature.units.repository;

import com.revature.models.Moon;
import com.revature.repository.MoonDao;
import com.revature.utilities.ConnectionUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MoonDaoTest {
    @Mock
    private static Connection connection;

    @Mock
    private static MockedStatic<ConnectionUtil> connectionUtils;

    private MoonDao dao;

    @BeforeAll
    public static void mockConnectionUtil() {
        connectionUtils = Mockito.mockStatic(ConnectionUtil.class);
    }

    @AfterAll
    public static void unmockConnectionUtil() {
        connectionUtils.close();
    }

    @BeforeEach
    public void setup() {
        connection = Mockito.mock(Connection.class);
        connectionUtils.when(ConnectionUtil::createConnection).thenReturn(connection);
        dao = new MoonDao();
    }

    @AfterEach
    public void cleanup() {
        connection = null;
        dao = null;
    }

    @Test
    @DisplayName("MoonDao::getAllMoons - Success")
    @Order(0)
    public void getAllMoonsSuccess() throws SQLException {
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);

        when(connection.prepareStatement("SELECT m.* FROM moons m JOIN planets p ON m.myPlanetId = p.id WHERE p.ownerId = ?")).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(results);
        doNothing().when(ps).setInt(1, 1);

        when(results.next()).thenReturn(true).thenReturn(false);
        when(results.getInt("id")).thenReturn(1);
        when(results.getString("name")).thenReturn("Moon1");
        when(results.getInt("myPlanetId")).thenReturn(1);

        List<Moon> moonList = dao.getAllMoons(1);

        assertNotNull(moonList);
        assertEquals(1, moonList.size());
        Moon moon = moonList.get(0);
        assertEquals(1, moon.getId());
        assertEquals("Moon1", moon.getName());
        assertEquals(1, moon.getMyPlanetId());
    }

    @Test
    @DisplayName("MoonDao::getAllMoons - Failure")
    @Order(1)
    public void getAllMoonsFailure() throws SQLException {
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);

        when(connection.prepareStatement("SELECT m.* FROM moons m JOIN planets p ON m.myPlanetId = p.id WHERE p.ownerId = ?")).thenReturn(ps);
        doNothing().when(ps).setInt(1, 1);

        // Mock the prepared statement to return the result set
        when(ps.executeQuery()).thenReturn(results);

        // Mock the result set to simulate no data found
        when(results.next()).thenReturn(false);

        List<Moon> moonList = dao.getAllMoons(1);

        assertEquals(0, moonList.size());
    }


    @ParameterizedTest
    @DisplayName("MoonDao::getMoonByName - Success")
    @Order(3)
    @CsvSource({
            "1,Moon1",
            "1,Moon2",
            "2,Moon3",
            "2,Moon4"
    })
    public void getMoonByNameSuccess(int ownerId, String name) throws SQLException {
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);

        when(connection.prepareStatement("SELECT * FROM moons WHERE name = ?")).thenReturn(ps);
        doNothing().when(ps).setString(1, name);

        when(ps.executeQuery()).thenReturn(results);

        when(results.next()).thenReturn(true);
        when(results.getInt("id")).thenReturn(1);
        when(results.getString("name")).thenReturn(name);
        when(results.getInt("myPlanetId")).thenReturn(1);

        Moon testedObj = dao.getMoonByName(name);

        assertEquals(name, testedObj.getName());
    }

    @Test
    @DisplayName("MoonDao::getMoonByName - Failure")
    @Order(4)
    public void getMoonByNameFailure() {
        int ownerId = 1;
        String name = "CannotFind";

        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("SELECT * FROM moons WHERE name = ?")).thenReturn(ps);

            doNothing().when(ps).setString(1, name);
            ResultSet results = Mockito.mock(ResultSet.class);
            when(ps.executeQuery()).thenReturn(results);
            when(results.next()).thenReturn(false);

            Moon testedObj = dao.getMoonByName(name);
            Assertions.assertNull(testedObj);
        } catch (SQLException e) {
            Assertions.fail("SQLException thrown.");
        }
    }

    @ParameterizedTest
    @DisplayName("MoonDao::getMoonById - Success")
    @Order(5)
    @CsvSource({
            "1",
            "2",
            "3",
            "4"
    })
    public void getMoonByIdSuccess(int moonId) {
        try {
            PreparedStatement ps = mock(PreparedStatement.class);
            ResultSet results = mock(ResultSet.class);

            when(connection.prepareStatement("SELECT * FROM moons WHERE id = ?")).thenReturn(ps);
            doNothing().when(ps).setInt(1, moonId);

            when(ps.executeQuery()).thenReturn(results);
            when(results.next()).thenReturn(true);
            when(results.getInt("id")).thenReturn(moonId);
            when(results.getString("name")).thenReturn("Placeholder");
            when(results.getInt("myPlanetId")).thenReturn(1);

            Moon actual = dao.getMoonById(moonId);

            assertEquals(moonId, actual.getId());
            assertEquals("Placeholder", actual.getName());
            assertEquals(1, actual.getMyPlanetId());
        } catch (SQLException e) {
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("MoonDao::getMoonById - Failure")
    @Order(6)
    public void getMoonByIdFailure() {
        int moonId = -1;

        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("SELECT * FROM moons WHERE id = ?")).thenReturn(ps);

            doNothing().when(ps).setInt(1, moonId);
            ResultSet results = Mockito.mock(ResultSet.class);
            when(ps.executeQuery()).thenReturn(results);
            when(results.next()).thenReturn(false);

            Moon testedObj = dao.getMoonById(moonId);
            Assertions.assertNull(testedObj);
        } catch (SQLException e) {
            Assertions.fail("SQLException thrown.");
        }
    }

    @ParameterizedTest
    @DisplayName("MoonDao::createMoon - Success")
    @Order(7)
    @CsvSource({
            "1,Moon1",
            "1,Moon2",
            "2,Moon3",
            "2,Moon4"
    })
    public void createMoonSuccess(int myPlanetId, String name) {
        try {
            // Mocking PreparedStatement and ResultSet for checking if planet exists
            PreparedStatement psCheckPlanetExists = mock(PreparedStatement.class);
            ResultSet resultsCheckPlanetExists = mock(ResultSet.class);

            // Mocking PreparedStatement and ResultSet for inserting moon
            PreparedStatement psInsert = mock(PreparedStatement.class);
            ResultSet resultsInsert = mock(ResultSet.class);

            // Mock the connection to return the prepared statement for checking if the planet exists
            when(connection.prepareStatement("SELECT COUNT(*) FROM planets WHERE id = ?")).thenReturn(psCheckPlanetExists);
            when(psCheckPlanetExists.executeQuery()).thenReturn(resultsCheckPlanetExists);
            when(resultsCheckPlanetExists.next()).thenReturn(true);
            when(resultsCheckPlanetExists.getInt(1)).thenReturn(1); // Simulate that the planet exists

            // Ensure setInt on the PreparedStatement is properly mocked
            doNothing().when(psCheckPlanetExists).setInt(1, myPlanetId);

            // Mock the connection to return the prepared statement for inserting the moon
            when(connection.prepareStatement("INSERT INTO moons (name, myPlanetId) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)).thenReturn(psInsert);
            when(psInsert.executeUpdate()).thenReturn(1);
            when(psInsert.getGeneratedKeys()).thenReturn(resultsInsert);
            when(resultsInsert.next()).thenReturn(true);
            when(resultsInsert.getInt(1)).thenReturn(1);

            // Ensure setString and setInt on the PreparedStatement are properly mocked
            doNothing().when(psInsert).setString(1, name);
            doNothing().when(psInsert).setInt(2, myPlanetId);

            // Create the expected moon object
            Moon expected = new Moon();
            expected.setName(name);
            expected.setMyPlanetId(myPlanetId);

            // Create a spy of the MoonDao to mock the isPlanetExist method
            MoonDao spyDao = Mockito.spy(dao);
            Mockito.doReturn(true).when(spyDao).isPlanetExist(Mockito.any(Connection.class), Mockito.eq(myPlanetId));

            // Call the method under test
            Moon actual = spyDao.createMoon(expected);

            // Assert that the returned moon object is correct
            assertEquals(name, actual.getName());
            assertEquals(myPlanetId, actual.getMyPlanetId());
            assertTrue(actual.getId() > 0);
        } catch (SQLException e) {
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("MoonDao::createMoon - Failure - Planet Does Not Exist")
    @Order(8)
    public void createMoonFailurePlanetNotExist() throws SQLException {
        int myPlanetId = -1;
        String name = "Placeholder";

        MoonDao spyDao = Mockito.spy(new MoonDao());

        // Mock the isPlanetExist method to return false
        Mockito.doReturn(false).when(spyDao).isPlanetExist(Mockito.any(Connection.class), Mockito.eq(myPlanetId));

        Moon expected = new Moon();
        expected.setName(name);
        expected.setMyPlanetId(myPlanetId);

        // Call the method under test
        Moon actual = spyDao.createMoon(expected);

        // Assert that the returned moon object is null due to the planet not existing
        Assertions.assertNull(actual);
    }

    @ParameterizedTest
    @DisplayName("MoonDao::deleteMoonById - Success")
    @Order(9)
    @CsvSource({
            "1",
            "2",
            "3",
            "4"
    })
    public void deleteMoonByIdSuccess(int moonId) {
        try {
            // Mocking PreparedStatement and ResultSet for checking if moon exists
            PreparedStatement psCheckMoonExists = mock(PreparedStatement.class);
            ResultSet resultsCheckMoonExists = mock(ResultSet.class);

            // Mocking PreparedStatement for deleting moon
            PreparedStatement psDelete = mock(PreparedStatement.class);

            // Mock the connection to return the prepared statement for checking if the moon exists
            when(connection.prepareStatement("SELECT * FROM moons WHERE id = ?")).thenReturn(psCheckMoonExists);
            when(psCheckMoonExists.executeQuery()).thenReturn(resultsCheckMoonExists);
            when(resultsCheckMoonExists.next()).thenReturn(true);

            // Ensure setInt on the PreparedStatement is properly mocked
            doNothing().when(psCheckMoonExists).setInt(1, moonId);

            // Mock the connection to return the prepared statement for deleting the moon
            when(connection.prepareStatement("DELETE FROM moons WHERE id = ?")).thenReturn(psDelete);
            when(psDelete.executeUpdate()).thenReturn(1);

            // Ensure setInt on the PreparedStatement is properly mocked
            doNothing().when(psDelete).setInt(1, moonId);

            // Create a spy of the MoonDao to mock the getMoonById method
            MoonDao spyDao = Mockito.spy(dao);
            Moon mockMoon = new Moon();
            mockMoon.setId(moonId);
            Mockito.doReturn(mockMoon).when(spyDao).getMoonById(moonId);

            // Call the method under test
            boolean result = spyDao.deleteMoonById(moonId);

            // Assert that the moon was deleted successfully
            Assertions.assertTrue(result);
        } catch (SQLException e) {
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("MoonDao::deleteMoonById - Failure")
    @Order(10)
    public void deleteMoonByIdFailure() {
        int moonId = -1;

        try {
            // Mocking PreparedStatement and ResultSet for checking if moon exists
            PreparedStatement psCheckMoonExists = mock(PreparedStatement.class);
            ResultSet resultsCheckMoonExists = mock(ResultSet.class);

            // Mocking PreparedStatement for deleting moon
            PreparedStatement psDelete = mock(PreparedStatement.class);

            // Mock the connection to return the prepared statement for checking if the moon exists
            when(connection.prepareStatement("SELECT * FROM moons WHERE id = ?")).thenReturn(psCheckMoonExists);
            when(psCheckMoonExists.executeQuery()).thenReturn(resultsCheckMoonExists);
            when(resultsCheckMoonExists.next()).thenReturn(false);

            // Ensure setInt on the PreparedStatement is properly mocked
            doNothing().when(psCheckMoonExists).setInt(1, moonId);

            // Mock the connection to return the prepared statement for deleting the moon
            when(connection.prepareStatement("DELETE FROM moons WHERE id = ?")).thenReturn(psDelete);
            when(psDelete.executeUpdate()).thenReturn(0);

            // Ensure setInt on the PreparedStatement is properly mocked
            doNothing().when(psDelete).setInt(1, moonId);

            // Create a spy of the MoonDao to mock the getMoonById method
            MoonDao spyDao = Mockito.spy(dao);
            Mockito.doReturn(null).when(spyDao).getMoonById(moonId);

            // Call the method under test
            boolean result = spyDao.deleteMoonById(moonId);

            // Assert that the moon was not deleted
            Assertions.assertFalse(result);
        } catch (SQLException e) {
            Assertions.fail("SQLException thrown.");
        }
    }

    @ParameterizedTest
    @DisplayName("MoonDao::getMoonsFromPlanet - Success")
    @Order(11)
    @CsvSource({
            "1",
            "2",
            "3",
            "4"
    })
    public void getMoonsFromPlanetSuccess(int planetId) {
        try {
            // Mocking PreparedStatement and ResultSet
            PreparedStatement ps = mock(PreparedStatement.class);
            ResultSet results = mock(ResultSet.class);

            // Mock the connection to return the prepared statement for getting moons from planet
            when(connection.prepareStatement("SELECT * FROM moons WHERE myPlanetId = ?")).thenReturn(ps);
            when(ps.executeQuery()).thenReturn(results);
            doNothing().when(ps).setInt(1, planetId);

            // Mock ResultSet behavior
            when(results.next()).thenReturn(true).thenReturn(false);
            when(results.getInt("id")).thenReturn(1);
            when(results.getString("name")).thenReturn("Placeholder");
            when(results.getInt("myPlanetId")).thenReturn(planetId);

            // Call the method under test
            List<Moon> moonList = dao.getMoonsFromPlanet(planetId);

            // Assert the results
            assertNotNull(moonList);
            assertEquals(1, moonList.size());
            Moon moon = moonList.get(0);
            assertEquals(1, moon.getId());
            assertEquals("Placeholder", moon.getName());
            assertEquals(planetId, moon.getMyPlanetId());
        } catch (SQLException e) {
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("MoonDao::getMoonsFromPlanet - Failure")
    @Order(12)
    public void getMoonsFromPlanetFailure() {
        int planetId = 1;
        try {
            // Mocking PreparedStatement and ResultSet
            PreparedStatement ps = mock(PreparedStatement.class);
            ResultSet results = mock(ResultSet.class);

            // Mock the connection to return the prepared statement for getting moons from planet
            when(connection.prepareStatement("SELECT * FROM moons WHERE myPlanetId = ?")).thenReturn(ps);
            when(ps.executeQuery()).thenReturn(results);
            doNothing().when(ps).setInt(1, planetId);

            // Mock ResultSet behavior
            when(results.next()).thenReturn(false);

            // Call the method under test
            List<Moon> moonList = dao.getMoonsFromPlanet(planetId);

            // Assert the results
            assertNotNull(moonList);
            assertEquals(0, moonList.size());
        } catch (SQLException e) {
            Assertions.fail("SQLException thrown.");
        }
    }
}