package com.revature.units.repository;

import com.revature.models.Planet;
import com.revature.repository.PlanetDao;
import com.revature.utilities.ConnectionUtil;
import io.cucumber.java.BeforeAll;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.*;
import java.util.List;

import static org.mockito.Mockito.*;

public class PlanetDaoTest {

    @Mock
    public static MockedStatic<ConnectionUtil> mockedStatic = Mockito.mockStatic(ConnectionUtil.class);

    @Mock
    public Connection conn;

    @Mock
    public PreparedStatement ps;

    @Mock
    public ResultSet rs;

    public PlanetDao planetDao;

    @AfterAll
    public static void unmockConnectionUtil() {
        mockedStatic.close();
    }

    @BeforeEach
    public void setUp() throws SQLException {
        conn = Mockito.mock(Connection.class);
        ps = Mockito.mock(PreparedStatement.class);
        rs = Mockito.mock(ResultSet.class);
        mockedStatic.when(ConnectionUtil::createConnection).thenReturn(conn);
        planetDao = new PlanetDao();
    }

    @AfterEach
    public void cleanup() {
        conn = null;
        planetDao = null;
    }

    @Test
    @DisplayName("GetAllPlanets::Valid")
    public void getAllPlanetsValid() throws SQLException {
        when(conn.prepareStatement("SELECT id, name, ownerId FROM planets WHERE ownerId = ?")).thenReturn(ps);
        doNothing().when(ps).setInt(1, 1);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("name")).thenReturn("Earth");
        when(rs.getInt("ownerId")).thenReturn(1);

        List<Planet> planetList = planetDao.getAllPlanets(1);

        Assertions.assertNotNull(planetList);
        Assertions.assertEquals(1, planetList.size());
        Assertions.assertEquals(1, planetList.get(0).getId());
        Assertions.assertEquals("Earth", planetList.get(0).getName());
        Assertions.assertEquals(1, planetList.get(0).getOwnerId());
    }

    @ParameterizedTest
    @DisplayName("GetPlanetByName::Valid")
    @CsvSource({
            "1, Earth",
            "1, Mars",
            "2, Mercury"
    })
    public void getPlanetByNameValid(int ownerId, String name) throws SQLException {
        when(conn.prepareStatement("SELECT id, name, ownerId FROM planets WHERE name = ? AND ownerId = ?")).thenReturn(ps);
        doNothing().when(ps).setString(1, name);
        doNothing().when(ps).setInt(2, ownerId);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("name")).thenReturn(name);
        when(rs.getInt("ownerId")).thenReturn(ownerId);

        Planet planet = planetDao.getPlanetByName(ownerId, name);

        Assertions.assertEquals(name, planet.getName());
    }

    @ParameterizedTest
    @DisplayName("GetPlanetById::Valid")
    @CsvSource({
            "1, 1",
            "1, 2",
            "2, 3"
    })
    public void getPlanetByIdValid(int ownerId, int planetId) throws SQLException {
        when(conn.prepareStatement("SELECT id, name, ownerId FROM planets WHERE id = ? AND ownerId = ?")).thenReturn(ps);
        doNothing().when(ps).setInt(1, planetId);
        doNothing().when(ps).setInt(2, ownerId);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getInt("id")).thenReturn(planetId);
        when(rs.getString("name")).thenReturn("PlanetFound");
        when(rs.getInt("ownerId")).thenReturn(ownerId);

        Planet planet = planetDao.getPlanetById(ownerId, planetId);

        Assertions.assertEquals(planetId, planet.getId());
    }

    @ParameterizedTest
    @DisplayName("CreatePlanet::Valid")
    @CsvSource({
            "1, Earth",
            "1, Mars",
            "2, Mercury"
    })
    public void createPlanetValid(int ownerId, String name) throws SQLException {
        when(conn.prepareStatement("INSERT INTO planets (name, ownerId) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)).thenReturn(ps);
        doNothing().when(ps).setString(1, name);
        doNothing().when(ps).setInt(2, ownerId);
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(1);

        Planet planet = new Planet();
        planet.setName(name);
        planet.setOwnerId(ownerId);

        Planet p = planetDao.createPlanet(planet);

        Assertions.assertTrue(planet.getId() > 0);
        Assertions.assertEquals(name, p.getName());
        Assertions.assertEquals(ownerId, p.getOwnerId());
    }

    @Test
    @DisplayName("DeletePlanetById::Valid")
    public void deletePlanetByIdValid() throws SQLException {
        int ownerId = 1;
        int planetId = 1;

        PreparedStatement check = Mockito.mock(PreparedStatement.class);
        PreparedStatement moonPs = Mockito.mock(PreparedStatement.class);
        ResultSet moonCheck = Mockito.mock(ResultSet.class);

        when(conn.prepareStatement("SELECT id, name, ownerId FROM planets WHERE id = ? AND ownerId = ?")).thenReturn(check);
        doNothing().when(check).setInt(1, planetId);
        doNothing().when(check).setInt(2, ownerId);
        when(check.executeQuery()).thenReturn(moonCheck);
        when(moonCheck.next()).thenReturn(true);

        when(conn.prepareStatement("DELETE FROM moons WHERE myPlanetId = ?")).thenReturn(moonPs);
        doNothing().when(moonPs).setInt(1, planetId);
        when(moonPs.executeUpdate()).thenReturn(1);

        when(conn.prepareStatement("DELETE FROM planets WHERE id = ? AND ownerId = ?")).thenReturn(ps);
        doNothing().when(ps).setInt(1, planetId);
        doNothing().when(ps).setInt(2, ownerId);
        when(ps.executeUpdate()).thenReturn(1);

        boolean deletion = planetDao.deletePlanetById(ownerId, planetId);

        Assertions.assertTrue(deletion);
    }
}
