package com.revature.units.controller;

import com.revature.controller.PlanetController;
import com.revature.models.Moon;
import com.revature.models.Planet;
import com.revature.models.User;
import com.revature.service.PlanetService;
import io.javalin.http.Context;
import io.javalin.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlanetControllerTest {
    private final Context ctx = mock(Context.class);
    @Mock
    PlanetService planetService;
    @InjectMocks
    PlanetController planetController;

    @BeforeEach
    public void setup() {
        planetService = Mockito.mock(PlanetService.class);
        planetController = new PlanetController(planetService);
    }

    @Test
    @DisplayName("GetAllPlanets::Positive")
    public void testGetAllPlanetsPositive() {
        User user = new User();
        user.setId(1);
        List<Planet> planetList = new ArrayList<>();
        Planet planet = new Planet();
        planet.setId(1);
        planet.setName("Earth");
        planetList.add(planet);

        when(ctx.sessionAttribute("user")).thenReturn(user);
        when(planetService.getAllPlanets(user.getId())).thenReturn(planetList);
        when(ctx.json(planetList)).thenReturn(ctx);

        planetController.getAllPlanets(ctx);

        Mockito.verify(ctx).json(planetList);
        Mockito.verify(ctx).status(200);
    }

    @Test
    @DisplayName("GetPlanetByName::Positive")
    public void testGetPlanetByNamePositive() {
        User user = new User();
        user.setId(1);
        Planet planet = new Planet();
        planet.setId(1);
        planet.setName("Earth");
        String planetName = "Earth";

        when(ctx.sessionAttribute("user")).thenReturn(user);
        when(ctx.pathParam("name")).thenReturn(planetName);
        when(planetService.getPlanetByName(user.getId(), planetName)).thenReturn(planet);
        when(ctx.json(planet)).thenReturn(ctx);

        planetController.getPlanetByName(ctx);

        Mockito.verify(ctx).json(planet);
        Mockito.verify(ctx).status(200);
    }

    @Test
    @DisplayName("GetPlanetByName::Negative - planet name is empty")
    public void testGetPlanetByNameNegativeEmpty() {
        User user = new User();
        String planetName = "";

        when(ctx.sessionAttribute("user")).thenReturn(user);
        when(ctx.pathParam("name")).thenReturn(planetName);
        when(ctx.json("Planet name cannot be empty")).thenReturn(ctx);

        planetController.getPlanetByName(ctx);

        Mockito.verify(ctx).json("Planet name cannot be empty");
        Mockito.verify(ctx).status(400);
    }

    @Test
    @DisplayName("GetPlanetByName::Negative - planet does not exist")
    public void testGetPlanetByNameNegativeNull() {
        User user = new User();
        String planetName = "Mars";

        when(ctx.sessionAttribute("user")).thenReturn(user);
        when(ctx.pathParam("name")).thenReturn(planetName);
        when(planetService.getPlanetByName(user.getId(), planetName)).thenReturn(null);
        when(ctx.json("Planet not found")).thenReturn(ctx);

        planetController.getPlanetByName(ctx);

        Mockito.verify(ctx).json("Planet not found");
        Mockito.verify(ctx).status(404);
    }

    @Test
    @DisplayName("GetPlanetById::Positive")
    public void testGetPlanetByIdPositive() {
        User user = new User();
        user.setId(1);
        Planet planet = new Planet();
        planet.setId(1);
        planet.setName("Earth");
        int planetId = 1;

        Validator<Integer> validator = mock(Validator.class);
        when(validator.get()).thenReturn(planetId);

        when(ctx.sessionAttribute("user")).thenReturn(user);
        when(ctx.pathParamAsClass("id", Integer.class)).thenReturn(validator);
        when(planetService.getPlanetById(user.getId(), planetId)).thenReturn(planet);
        when(ctx.json(planet)).thenReturn(ctx);

        planetController.getPlanetByID(ctx);

        Mockito.verify(ctx).json(planet);
        Mockito.verify(ctx).status(200);
    }

    @Test
    @DisplayName("CreatePlanet::Positive")
    public void testCreatePlanetPositive() {
        User user = new User();
        user.setId(1);
        Planet planet = new Planet();
        planet.setId(1);
        planet.setName("Earth");

        when(ctx.bodyAsClass(Planet.class)).thenReturn(planet);
        when(ctx.sessionAttribute("user")).thenReturn(user);
        when(planetService.createPlanet(user.getId(), planet)).thenReturn(planet);
        when(ctx.json(planet)).thenReturn(ctx);

        planetController.createPlanet(ctx);

        Mockito.verify(ctx).json(planet);
        Mockito.verify(ctx).status(201);
    }

    @Test
    @DisplayName("DeletePlanet::Positive")
    public void testDeletePlanetPositive() {
        User user = new User();
        user.setId(1);
        int planetId = 1;

        Validator<Integer> validator = mock(Validator.class);
        when(validator.get()).thenReturn(planetId);

        when(ctx.sessionAttribute("user")).thenReturn(user);
        when(ctx.pathParamAsClass("id", Integer.class)).thenReturn(validator);
        when(planetService.deletePlanetById(user.getId(), planetId)).thenReturn(true);
        when(ctx.json("Planet successfully deleted")).thenReturn(ctx);

        planetController.deletePlanet(ctx);

        Mockito.verify(ctx).json("Planet successfully deleted");
        Mockito.verify(ctx).status(202);
    }

    @Test
    @DisplayName("DeletePlanet::Negative")
    public void testDeletePlanetNegative() {
        User user = new User();
        user.setId(1);
        int planetId = 1;

        Validator<Integer> validator = mock(Validator.class);
        when(validator.get()).thenReturn(planetId);

        when(ctx.sessionAttribute("user")).thenReturn(user);
        when(ctx.pathParamAsClass("id", Integer.class)).thenReturn(validator);
        when(planetService.deletePlanetById(user.getId(), planetId)).thenReturn(false);
        when(ctx.json("Failed to delete planet")).thenReturn(ctx);

        planetController.deletePlanet(ctx);

        Mockito.verify(ctx).json("Failed to delete planet");
        Mockito.verify(ctx).status(500);
    }
}