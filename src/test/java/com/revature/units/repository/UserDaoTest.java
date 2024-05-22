package com.revature.units.repository;

import com.revature.models.User;
import com.revature.models.UsernamePasswordAuthentication;
import com.revature.repository.UserDao;
import com.revature.utilities.ConnectionUtil;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class UserDaoTest {

    @Mock
    static MockedStatic<ConnectionUtil> connectionUtil;
    @Mock
    static Connection connection;
    @Mock
    PreparedStatement ps;
    @Mock
    ResultSet resultSet;

    int userId = 100;
    UserDao userDao;

    @BeforeAll
    public static void setUpClass() throws Exception {
        connectionUtil = Mockito.mockStatic(ConnectionUtil.class);
    }

    @AfterAll
    public static void tearDownClass() {
        connectionUtil.close();
    }

    @BeforeEach
    public void setUp() throws SQLException {
        connection = Mockito.mock(Connection.class);
        ps = Mockito.mock(PreparedStatement.class);
        resultSet = Mockito.mock(ResultSet.class);

        connectionUtil.when(ConnectionUtil::createConnection).thenReturn(connection);
    }

    @AfterEach
    public void tearDown() {
//        connection = null;
//        userDao = null;
    }

    @Test
    @DisplayName("UserDao::CreateUser - success")
    public void testCreateUserPositive() throws SQLException {

        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(ps);
        doNothing().when(ps).setString(anyInt(), anyString());
        when(ps.executeUpdate()).thenReturn(1);

        when(ps.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(Boolean.TRUE);
        when(resultSet.getInt(anyInt())).thenReturn(userId);

        // act
        UsernamePasswordAuthentication uauth = new UsernamePasswordAuthentication();
        uauth.setUsername("validName");
        uauth.setPassword("validPassword");

        UserDao userDao = new UserDao();
        User actual = userDao.createUser(uauth);

        //verify and assert
        Mockito.verify(connection, Mockito.times(1)).prepareStatement(anyString(), anyInt());
        Mockito.verify(ps, Mockito.times(2)).setString(anyInt(), anyString());
        Mockito.verify(ps, Mockito.times(1)).executeUpdate();
        Mockito.verify(resultSet, Mockito.times(1)).next();
        Mockito.verify(resultSet, Mockito.times(1)).getInt(1);

        Assertions.assertEquals(userId, actual.getId());
        Assertions.assertEquals("validName", actual.getUsername());
    }

    @Test
    @DisplayName("UserDao::CreateUser - failure")
    public void testCreateUserNegative() throws SQLException {
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(ps);
        doNothing().when(ps).setString(anyInt(), anyString());
        when(ps.executeUpdate()).thenReturn(1);

        when(ps.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(Boolean.FALSE);

        // act
        UsernamePasswordAuthentication uauth = new UsernamePasswordAuthentication();
        uauth.setUsername("         ");
        uauth.setPassword("validPassword");
        userDao = new UserDao();
        User actual = userDao.createUser(uauth);

        //verify and assert
        Mockito.verify(connection, Mockito.times(1)).prepareStatement(anyString(), anyInt());
        Mockito.verify(ps, Mockito.times(2)).setString(anyInt(), anyString());
        Mockito.verify(ps, Mockito.times(1)).executeUpdate();
        Mockito.verify(resultSet, Mockito.times(1)).next();

        Assertions.assertEquals(new User(), actual);
    }

    @Test
    @DisplayName("UserDao::GetUserByUsername - success")
    public void testGetUserByUsernamePositive() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        doNothing().when(ps).setString(anyInt(), anyString());

        when(ps.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(Boolean.TRUE);
        when(resultSet.getInt(anyString())).thenReturn(userId);
        when(resultSet.getString("username")).thenReturn("validName");
        when(resultSet.getString("password")).thenReturn("validPassword");

        // act
        User userCreated = new User();
        userCreated.setId(userId);
        userCreated.setUsername("validName");
        userCreated.setPassword("validPassword");
        userDao = new UserDao();
        User actual = userDao.getUserByUsername(userCreated.getUsername());

        //verify and assert
        Mockito.verify(connection, Mockito.times(1)).prepareStatement(anyString());
        Mockito.verify(ps, Mockito.times(1)).setString(anyInt(), anyString());
        Mockito.verify(ps, Mockito.times(1)).executeQuery();
        Mockito.verify(resultSet, Mockito.times(1)).next();
        Mockito.verify(resultSet, Mockito.times(1)).getInt(anyString());
        Mockito.verify(resultSet, Mockito.times(2)).getString(anyString());

        Assertions.assertEquals(userCreated, actual);
    }

    @Test
    @DisplayName("UserDao::GetUserByUsername - failure")
    public void testGetUserByUsernameNegative() throws SQLException {
        ps = Mockito.mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        doNothing().when(ps).setString(anyInt(), anyString());

        resultSet = Mockito.mock(ResultSet.class);
        when(ps.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(Boolean.FALSE);

        // act
        User userCreated = new User();
        userCreated.setId(userId);
        userCreated.setUsername("validName");
        userCreated.setPassword("validPassword");
        userDao = new UserDao();
        User actual = userDao.getUserByUsername(userCreated.getUsername());

        //verify and assert
        Mockito.verify(connection, Mockito.times(1)).prepareStatement(anyString());
        Mockito.verify(ps, Mockito.times(1)).setString(anyInt(), anyString());
        Mockito.verify(ps, Mockito.times(1)).executeQuery();
        Mockito.verify(resultSet, Mockito.times(1)).next();

        Assertions.assertNull(actual);
    }


}
