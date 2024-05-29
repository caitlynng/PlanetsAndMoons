package com.revature.units.service;

import com.revature.models.User;
import com.revature.models.UsernamePasswordAuthentication;
import com.revature.repository.UserDao;
import com.revature.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    @Mock
    UserDao userDao;
    @InjectMocks
    UserService userService;

    @ParameterizedTest
    @DisplayName("Register::Valid - LowerCase")
    @CsvSource({
            "user, pass",
            "user123, pass123"
    })
    @Order(1)
    public void registerValidLowerCase(String uName, String pwd) {
        //Arrange
        UsernamePasswordAuthentication user = new UsernamePasswordAuthentication();
        user.setUsername(uName);
        user.setPassword(pwd);

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername(uName);
        mockUser.setPassword(pwd);

        when(userDao.createUser(user)).thenReturn(mockUser);

        //Act
        User actualUser = userService.register(mockUser);

        //Assert
        Mockito.verify(this.userDao, times(1)).createUser(user);
        assertEquals(mockUser, actualUser);
    }

    @ParameterizedTest
    @DisplayName("Register::Valid - UpperCase")
    @CsvSource({
            "USER, pass",
            "USER123, PASS123"
    })
    @Order(2)
    public void registerValidUpperCase(String uName, String pwd) {
        //Arrange
        UsernamePasswordAuthentication user = new UsernamePasswordAuthentication();
        user.setUsername(uName.toLowerCase());
        user.setPassword(pwd);

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername(uName);
        mockUser.setPassword(pwd);
        when(userDao.createUser(user)).thenReturn(mockUser);

        //Act
        User actualUser = userService.register(mockUser);

        //Assert
        Mockito.verify(this.userDao, times(1)).createUser(user);
        assertEquals(mockUser, actualUser);
    }

    @ParameterizedTest
    @DisplayName("Register::Valid - TrailingSpace")
    @CsvSource({
            "user  , pass",
            "  user123, Pass123"
    })
    @Order(3)
    public void registerValidTrailingSpace(String uName, String pwd) {
        //Arrange
        UsernamePasswordAuthentication user = new UsernamePasswordAuthentication();
        user.setUsername(uName.trim());
        user.setPassword(pwd);

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername(uName);
        mockUser.setPassword(pwd);
        when(userDao.createUser(user)).thenReturn(mockUser);

        //Act
        User actualUser = userService.register(mockUser);

        //Assert
        Mockito.verify(this.userDao, times(1)).createUser(user);
        assertEquals(mockUser, actualUser);
    }

    @ParameterizedTest
    @DisplayName("Register::Invalid - EmptyField")
    @CsvSource({
            "'', password",
            "user, '",
            "'',''"
    })
    @Order(4)
    public void registerInvalidEmptyField(String uName, String pwd) {
        //Arrange
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername(uName);
        mockUser.setPassword(pwd);

        //Act
        User actualUser = userService.register(mockUser);

        // Assert that no user was created (null)
        assertNull(actualUser);
    }

    @ParameterizedTest
    @DisplayName("Register::Invalid - DuplicateUsername")
    @CsvSource({
            "user, pass123",
            "user2, pass123"
    })
    @Order(5)
    public void registerInvalidDuplicateUsername(String uName, String pwd) {
        //Arrange
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setUsername(uName);
        existingUser.setPassword(pwd);

        User mockUser = new User();
        mockUser.setId(2);
        mockUser.setUsername(uName);
        mockUser.setPassword(pwd);

        when(userDao.getUserByUsername(uName)).thenReturn(existingUser);

        // Act
        User actualUser = userService.register(mockUser);

        // Assert that no user was created (null) due to duplicate username
        assertNull(actualUser);
    }

    @ParameterizedTest
    @DisplayName("Register::Invalid - FieldsTooLong")
    @CsvSource({
            "thisusernameisover30characterlong, pass123",
            "user, thisusernameisover31characterlong"
    })
    @Order(6)
    public void registerInvalidFieldsTooLong(String uName, String pwd) {
        //Arrange
        User mockUser = new User();
        mockUser.setId(2);
        mockUser.setUsername(uName);
        mockUser.setPassword(pwd);

        // Act
        User actualUser = userService.register(mockUser);

        // Assert that no user was created (null) due to exceeding field length
        assertNull(actualUser);
    }

    @ParameterizedTest
    @DisplayName("Register::Invalid - SQLInjection")
    @CsvSource({
            "Robert'; DROP TABLE users;--, pass123",
            "user, Robert'; DROP TABLE users;-- "
    })
    @Order(7)
    public void registerInvalidSQLInjection(String uName, String pwd) {
        //Arrange
        User mockUser = new User();
        mockUser.setId(2);
        mockUser.setUsername(uName);
        mockUser.setPassword(pwd);

        // Act
        User actualUser = userService.register(mockUser);

        // Assert that no user was created (null) due to SQL pattern
        assertNull(actualUser);
    }

    @ParameterizedTest
    @DisplayName("Authenticate::Valid")
    @CsvSource({
            "user, pass123",
            "user123, pass123"
    })
    @Order(8)
    public void authenticateValid(String uName, String pwd) {
        //Arrange
        UsernamePasswordAuthentication user = new UsernamePasswordAuthentication();
        user.setUsername(uName);
        user.setPassword(pwd);

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername(uName);
        mockUser.setPassword(pwd);
        //authenticate use dao.getUserByUsername to retrieve the user
        when(userDao.getUserByUsername(uName)).thenReturn(mockUser);

        //Act
        User authenticatedUser = userService.authenticate(user);

        //Assert that authenticate returns correct user if credentials are valid
        assertNotNull(authenticatedUser);
        assertEquals(mockUser.getUsername(), authenticatedUser.getUsername());
        assertEquals(mockUser.getPassword(), authenticatedUser.getPassword());
    }

    @ParameterizedTest
    @DisplayName("Authenticate::InvalidPassword")
    @CsvSource({
            "user, wrongPass",
    })
    @Order(9)
    public void authenticateInvalidPassword(String uName, String pwd) {
        //Arrange
        UsernamePasswordAuthentication user = new UsernamePasswordAuthentication();
        user.setUsername(uName);
        user.setPassword(pwd);

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("user");
        mockUser.setPassword("pass");
        //authenticate use dao.getUserByUsername to retrieve the user
        when(userDao.getUserByUsername(uName)).thenReturn(mockUser);

        //Act
        User authenticatedUser = userService.authenticate(user);

        //Assert that authenticate returns null if credentials are invalid
        assertNull(authenticatedUser);
    }

    @ParameterizedTest
    @DisplayName("Authenticate::InvalidUsername")
    @CsvSource({
            "wrongUser, pass",
    })
    @Order(10)
    public void authenticateInvalidUsername(String uName, String pwd) {
        //Arrange
        UsernamePasswordAuthentication user = new UsernamePasswordAuthentication();
        user.setUsername(uName);
        user.setPassword(pwd);

        when(userDao.getUserByUsername(uName.toLowerCase())).thenReturn(null);

        //Act
        User authenticatedUser = userService.authenticate(user);

        //Assert that authenticate returns null if credentials are invalid
        assertNull(authenticatedUser);
    }
}
