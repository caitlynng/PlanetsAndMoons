package com.revature.integrations.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.models.Moon;
import com.revature.models.Planet;
import com.revature.models.User;
import com.revature.utilities.ConnectionUtil;
import com.revature.utilities.RequestMapper;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.*;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlanetControllerIntegrationTest {
    static User user;
    static Planet planet;
    static Moon moon;
    Javalin app;
    static String hostUrl;
    OkHttpClient httpClient;
    ConcurrentHashMap<String, List<Cookie>> cookieStore;

    @BeforeAll
    public static void setupClass() throws SQLException {
        ConnectionUtil.deleteTables();
        Map<String, Object> data = ConnectionUtil.dataSeeding();
        user = (User) data.get("user");
        planet = (Planet) data.get("planet");
        moon = (Moon) data.get("moon");
    }

//    @AfterAll
//    public static void teardownClass() throws  SQLException{
//        ConnectionUtil.deleteTables();
//    }

    @BeforeEach
    public void setup() throws SQLException {
        app = Javalin.create();
        RequestMapper.setUpEndPoints(app);
        // Set up the cookie jar to manage cookies (sessions)
        cookieStore = new ConcurrentHashMap<>();

        CookieJar cookieJar = new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<>();
            }
        };
        httpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();
    }

    private void login(String hostUrl) throws Exception {
        RequestBody userBody = RequestBody.create(
                MediaType.get("application/json"),
                String.format("{\"username\":\"%s\", \"password\":\"%s\"}", user.getUsername(), user.getPassword())
        );
        Request loginRequest = new Request.Builder()
                .url(hostUrl + "/login")
                .post(userBody)
                .build();
        try (Response loginResponse = httpClient.newCall(loginRequest).execute()) {
            Assertions.assertEquals(202, loginResponse.code());
            assertThat(cookieStore.get("localhost")).isNotNull().isNotEmpty();
        }
    }

    private Response executeRequest(String hostUrl, String endpoint, String jsonBody, String method) throws Exception {
        RequestBody body = jsonBody != null ? RequestBody.create(MediaType.get("application/json"), jsonBody) : null;
        Request request = new Request.Builder()
                .url(hostUrl + endpoint)
                .method(method, body)
                .build();
        return httpClient.newCall(request).execute();
    }

    @Test
    @DisplayName("GetAllPlanets::Positive")
    public void testGetAllPlanetsPositive() {
        JavalinTest.test(app, (server, client) -> {
            String hostUrl = "http://localhost:" + server.port();
            login(hostUrl);
            try (Response response = executeRequest(hostUrl, "/api/planet", null, "GET")) {
                Assertions.assertEquals(200, response.code());
                ObjectMapper om = new ObjectMapper();
                List<Planet> planets = om.readValue(response.body().string(), new TypeReference<List<Planet>>() {});
                Assertions.assertFalse(planets.isEmpty());
            }
        });
    }

    @Test
    @Order(2)
    @DisplayName("GetPlanetByName::Positive")
    public void testGetPlanetByNamePositive() {
        JavalinTest.test(app, (server, client) -> {
            String hostUrl = "http://localhost:" + server.port();
            login(hostUrl);
            try (Response response = executeRequest(hostUrl, "/api/planet/name/" + planet.getName(), null, "GET")) {
                Assertions.assertEquals(200, response.code());
                ObjectMapper om = new ObjectMapper();
                Planet p = om.readValue(response.body().string(), Planet.class);
                Assertions.assertEquals(planet, p);
            }
        });
    }

    @Test
    @Order(3)
    @DisplayName("GetPlanetById::Positive")
    public void testGetPlanetByIdPositive() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            String hostUrl = "http://localhost:" + server.port();
            login(hostUrl);
            try (Response response = executeRequest(hostUrl, "/api/planet/id/" + planet.getId(), null, "GET")) {
                Assertions.assertEquals(200, response.code());
                ObjectMapper om = new ObjectMapper();
                Planet p = om.readValue(response.body().string(), Planet.class);
                Assertions.assertEquals(planet, p);
            }
        });
    }

    @Test
    @Order(1)
    @DisplayName("CreatePlanet::Positive")
    public void testCreatePlanetPositive() {
        String planetString = String.format("{\"name\":\"planetTest\", \"id\": %d}", planet.getOwnerId());
        JavalinTest.test(app, (server, client) -> {
            String hostUrl = "http://localhost:" + server.port();
            login(hostUrl);
            try (Response response = executeRequest(hostUrl, "/api/planet", planetString, "POST")) {
                Assertions.assertEquals(201, response.code());
            }
        });
    }

    @Test
    @DisplayName("DeletePlanet::Positive")
    public void testDeletePlanetPositive() {
        JavalinTest.test(app, (server, client) -> {
            String hostUrl = "http://localhost:" + server.port();
            login(hostUrl);
            try (Response response = executeRequest(hostUrl, "/api/planet/" + planet.getId(), null, "DELETE")) {
                Assertions.assertEquals(202, response.code());
            }
        });
    }
}
