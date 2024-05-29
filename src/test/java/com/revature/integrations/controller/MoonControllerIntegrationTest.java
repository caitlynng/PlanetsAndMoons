package com.revature.integrations.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.utilities.ConnectionUtil;
import com.revature.utilities.RequestMapper;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.*;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.revature.selenium.utils.DriverUtils.cleanDatabaseTable;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MoonControllerIntegrationTest {
    Javalin app;
    int planetId;

    @BeforeAll
    public static void setupClass() throws SQLException {
        cleanDatabaseTable();
    }

    @AfterAll
    public static void teardownClass() throws  SQLException{
        cleanDatabaseTable();
    }


    @BeforeEach
    public void setup() throws SQLException {
        app = Javalin.create();
        RequestMapper.setUpEndPoints(app);
    }

    @Test
    @Order(1)
    public void testDataSeeding(){
        JavalinTest.test(app, (server, client) -> {

            Map<String, String> userJson = new HashMap<>();
            userJson.put("username", "APIsTestUser");
            userJson.put("password", "validPassword");
            String userString = "{\"username\":\"APIsTestUser\", \"password\":\"validPassword\"}";

            String planetString = "{\"name\":\"Earth\", \"ownerId\": 1}";

            String hostUrl = "http://localhost:"+server.port();

            int actualStatusCode;
            String responseBody;
            try (Response response = client.post("/register", userJson)) {
                actualStatusCode = response.code();
                responseBody = Objects.requireNonNull(response.body().string());
                System.out.println(actualStatusCode + " :::: " + responseBody);
                Assertions.assertEquals(201, actualStatusCode);
            }


            // Set up the cookie jar to manage cookies (sessions)
            ConcurrentHashMap<String, List<Cookie>> cookieStore = new ConcurrentHashMap<>();

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

            OkHttpClient httpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();


            RequestBody userBody = RequestBody.create(
                    userString,
                    MediaType.get("application/json")
            );
            Request loginRequest = new Request.Builder()
                    .url(hostUrl+ "/login")
                    .post(userBody)
                    .build();
            try(Response loginResponse = httpClient.newCall(loginRequest).execute()){
                actualStatusCode = loginResponse.code();
                responseBody = Objects.requireNonNull(loginResponse.body().string());
                System.out.println(actualStatusCode + " :::: " + responseBody);
                Assertions.assertEquals(202, actualStatusCode);

                // Extract session cookie
                List<Cookie> cookies = cookieStore.get("localhost");
                org.assertj.core.api.Assertions.assertThat(cookies).isNotNull();
                org.assertj.core.api.Assertions.assertThat(cookies.size()).isGreaterThan(0);

                RequestBody planetBody = RequestBody.create(
                        planetString,
                        MediaType.get("application/json")
                );
                Request planetRequest = new Request.Builder()
                        .url(hostUrl+ "/api/planet")
                        .post(planetBody)
                        .build();
                try(Response planetResponse = httpClient.newCall(planetRequest).execute()){
                    actualStatusCode = planetResponse.code();
                    responseBody = Objects.requireNonNull(planetResponse.body().string());
                    System.out.println(actualStatusCode + " :::: " + responseBody);
                    Assertions.assertEquals(201, actualStatusCode);

                    ObjectMapper om = new ObjectMapper();
                    JsonNode jsonNode = om.readTree(responseBody);
                    planetId = jsonNode.get("id").asInt();
//                    System.out.println(planetId);
                }
            }

//            Assertions.assertNotNull(null);
        });

    }

}
