package com.revature.integrations.controller;

import com.revature.utilities.ConnectionUtil;
import com.revature.utilities.RequestMapper;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.Response;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static com.revature.selenium.utils.DriverUtils.cleanDatabaseTable;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerIntegrationTest {
    public Javalin app;

    @BeforeAll
    public static void setupClass() {
        cleanDatabaseTable();
    }

    @AfterAll
    public static void teardownClass() {
        cleanDatabaseTable();
    }

    @BeforeEach
    public void setup() throws SQLException {
        app = Javalin.create();
        RequestMapper.setUpEndPoints(app);
    }

    @Test
    @DisplayName("Register::Positive")
    @Order(1)
    public void testUserRegisterPositive() {
//        int randomNum = ThreadLocalRandom.current().nextInt(1000, 9999);
        JavalinTest.test(app, (server, client) -> {
            Map<String, String> requestJSON = new HashMap<>();
            requestJSON.put("username", "APIsTestUser");
            requestJSON.put("password", "validPassword");

            int actualStatusCode;
            String responseBody;
            try (Response response = client.post("/register", requestJSON)) {
                actualStatusCode = response.code();
                responseBody = Objects.requireNonNull(response.body().string());
//                System.out.println(actualStatusCode + " :::: " + responseBody);
            }
            Assertions.assertEquals(201, actualStatusCode);
            Assertions.assertNotNull(responseBody);
//            System.out.println(responseBody);
        });
    }

    @Test
    @DisplayName("Login::Positive")
    @Order(2)
    public void testUserLoginPositive() {
        JavalinTest.test(app, (server, client) -> {
            Map<String, String> requestJSON = new HashMap<>();
            requestJSON.put("username", "apistestuser");
            requestJSON.put("password", "validPassword");
            int actualStatusCode;
            String responseBody;
            try (Response response = client.post("/login", requestJSON)) {
                actualStatusCode = response.code();
                responseBody = Objects.requireNonNull(response.body().string());
                System.out.println(actualStatusCode + " :::: " + responseBody);
            }
            Assertions.assertEquals(202, actualStatusCode);
        });
    }

    @Test
    @DisplayName("Login::Negative - invalid password")
    public void testUserLoginNegative() {
        JavalinTest.test(app, (server, client) -> {
            Map<String, String> requestJSON = new HashMap<>();
            requestJSON.put("username", "apistestuser");
            requestJSON.put("password", "invalid");
            int actualStatusCode;
            String responseBody;
            try (Response response = client.post("/login", requestJSON)) {
                actualStatusCode = response.code();
                responseBody = Objects.requireNonNull(response.body().string());
                System.out.println(actualStatusCode + " :::: " + responseBody);
            }
            Assertions.assertEquals(400, actualStatusCode);
        });
    }

    @Test
    @DisplayName("Register::Negative - Blank Input")
    public void testUserRegisterNegativeBlankInput() {
        JavalinTest.test(app, (server, client) -> {
            Map<String, String> requestJSON = new HashMap<>();
            requestJSON.put("username", "           ");
            requestJSON.put("password", "valid");
            int actualStatusCode;
            String responseBody;
            try (Response response = client.post("/register", requestJSON)) {
                actualStatusCode = response.code();
                responseBody = Objects.requireNonNull(response.body().string());
                System.out.println(actualStatusCode + " :::: " + responseBody);
            }
            Assertions.assertEquals(400, actualStatusCode);
//            Assertions.assertNull(responseBody);
        });
    }

}
