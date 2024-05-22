package com.revature.integrations.service;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import com.revature.models.User;
import com.revature.models.UsernamePasswordAuthentication;
import com.revature.repository.UserDao;
import com.revature.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static com.revature.selenium.utils.DriverUtils.*;

@TestMethodOrder(OrderAnnotation.class)
public class UserServiceIntegrationTest {

    private UserDao userDao;
    private UserService userService;

    @BeforeEach
    public void setUp() throws SQLException {
        cleanDatabaseTable();

        userDao = new UserDao();
        userService = new UserService(userDao);
    }

    @Test
    @DisplayName("Register::Positive")
    @Order(1)
    public void testRegisterIntegration() throws SQLException {
        // Arrange
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("password");

        // Act
        User registeredUser = userService.register(newUser);

        // Assert
        assertNotNull(registeredUser);
        assertEquals(newUser.getUsername().toLowerCase(), registeredUser.getUsername());
        assertEquals(newUser.getPassword(), registeredUser.getPassword());
    }

    @Test
    @DisplayName("Authenticate::Positive")
    @Order(2)
    public void testAuthenticateIntegration() throws SQLException {
        // Arrange
        UsernamePasswordAuthentication newUser = new UsernamePasswordAuthentication();
        newUser.setUsername("testuser");
        newUser.setPassword("password");

        userDao.createUser(newUser);

        // Act
        User authenticatedUser = userService.authenticate(newUser);

        // Assert
        assertNotNull(authenticatedUser);
        assertEquals(newUser.getUsername().toLowerCase(), authenticatedUser.getUsername());
        assertEquals(newUser.getPassword(), authenticatedUser.getPassword());
    }

    @Test
    @DisplayName("Authenticate::Negative - Invalid Username")
    @Order(3)
    public void testAuthenticateInvalidUsernameIntegration() {
        // Arrange
        UsernamePasswordAuthentication loginRequest = new UsernamePasswordAuthentication();
        loginRequest.setUsername("invaliduser");
        loginRequest.setPassword("password");

        // Act
        User authenticatedUser = userService.authenticate(loginRequest);

        // Assert
        assertNull(authenticatedUser);
    }

    @Test
    @DisplayName("Authenticate::Negative - Invalid Password")
    @Order(4)
    public void testAuthenticateInvalidPasswordIntegration() throws SQLException {
        // Arrange
        UsernamePasswordAuthentication newUser = new UsernamePasswordAuthentication();
        newUser.setUsername("testuser");
        newUser.setPassword("password");

        userDao.createUser(newUser);

        UsernamePasswordAuthentication loginRequest = new UsernamePasswordAuthentication();
        loginRequest.setUsername(newUser.getUsername());
        loginRequest.setPassword("wrongpassword");

        // Act
        User authenticatedUser = userService.authenticate(loginRequest);

        // Assert
        assertNull(authenticatedUser);
    }

    @Test
    @DisplayName("Register::Negative - Empty Username")
    @Order(5)
    public void testRegisterEmptyUsernameIntegration() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("");
        newUser.setPassword("password");

        // Act
        User registeredUser = userService.register(newUser);

        // Assert
        assertNull(registeredUser);
    }

    @Test
    @DisplayName("Register::Negative - Empty Password")
    @Order(6)
    public void testRegisterEmptyPasswordIntegration() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("");

        // Act
        User registeredUser = userService.register(newUser);

        // Assert
        assertNull(registeredUser);
    }

    @Test
    @DisplayName("Register::Negative - Username Too Long")
    @Order(7)
    public void testRegisterUsernameTooLongIntegration() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("thisusernameiswaytoolongandshouldfail");
        newUser.setPassword("password");

        // Act
        User registeredUser = userService.register(newUser);

        // Assert
        assertNull(registeredUser);
    }

    @Test
    @DisplayName("Register::Negative - Password Too Long")
    @Order(8)
    public void testRegisterPasswordTooLongIntegration() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("thispasswordiswaytoolongandshouldfail");

        // Act
        User registeredUser = userService.register(newUser);

        // Assert
        assertNull(registeredUser);
    }

    @Test
    @DisplayName("Register::Negative - Non-ASCII Characters in Username")
    @Order(9)
    public void testRegisterNonAsciiUsernameIntegration() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("usérñame");
        newUser.setPassword("password");

        // Act
        User registeredUser = userService.register(newUser);

        // Assert
        assertNull(registeredUser);
    }

    @Test
    @DisplayName("Register::Negative - Non-ASCII Characters in Password")
    @Order(10)
    public void testRegisterNonAsciiPasswordIntegration() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("pássword");

        // Act
        User registeredUser = userService.register(newUser);

        // Assert
        assertNull(registeredUser);
    }

    @Test
    @DisplayName("Register::Negative - SQL Injection in Username")
    @Order(11)
    public void testRegisterSqlInjectionUsernameIntegration() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("testuser; DROP TABLE users;");
        newUser.setPassword("password");

        // Act
        User registeredUser = userService.register(newUser);

        // Assert
        assertNull(registeredUser);
    }

    @Test
    @DisplayName("Register::Negative - SQL Injection in Password")
    @Order(12)
    public void testRegisterSqlInjectionPasswordIntegration() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("password; DROP TABLE users;");

        // Act
        User registeredUser = userService.register(newUser);

        // Assert
        assertNull(registeredUser);
    }

    @Test
    @DisplayName("Register::Negative - Duplicate Username")
    @Order(13)
    public void testRegisterDuplicateUsernameIntegration() throws SQLException {
        // Arrange
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("password");

        userService.register(newUser);

        // Act
        User duplicateUser = userService.register(newUser);

        // Assert
        assertNull(duplicateUser);
    }
}
