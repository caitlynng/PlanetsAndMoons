package com.revature.units.controller;

import com.revature.controller.MoonController;
import com.revature.models.Moon;
import com.revature.models.User;
import com.revature.service.MoonService;
import io.javalin.http.Context;
import io.javalin.validation.Validator;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MoonControllerTest {
    private final Context ctx = mock(Context.class);
    @Mock
    MoonService moonService;
    @InjectMocks
    MoonController moonController;

    @BeforeEach
    public void setup() {
        moonService = Mockito.mock(MoonService.class);
        moonController = new MoonController(moonService);
    }

    @Test
    @DisplayName("GetAllMoons::Positive")
    public void testGetAllMoonsPositive() {
        User user = new User();
        user.setId(1);
        List<Moon> moonList = new ArrayList<>();
        Moon moon = new Moon();
        moon.setId(1);
        moon.setName("moon1");
        moonList.add(moon);

        when(ctx.sessionAttribute("user")).thenReturn(user);
        when(moonService.getAllMoons(user.getId())).thenReturn(moonList);
        when(ctx.json(moonList)).thenReturn(ctx);

        moonController.getAllMoons(ctx);

        Mockito.verify(ctx).json(moonList);
        Mockito.verify(ctx).status(200);
    }

    @Test
    @DisplayName("GetMoonByName::Positive")
    public void testGetMoonByNamePositive() {
        User user = new User();
        user.setId(1);
        Moon moon = new Moon();
        moon.setId(1);
        moon.setName("moon1");

        when(ctx.sessionAttribute("user")).thenReturn(user);
        when(ctx.pathParam("name")).thenReturn("moon1");
        when(moonService.getMoonByName(user.getId(), "moon1")).thenReturn(moon);
        when(ctx.json(moon)).thenReturn(ctx);

        moonController.getMoonByName(ctx);

        Mockito.verify(ctx).json(moon);
        Mockito.verify(ctx).status(200);
    }

    @Test
    @DisplayName("GetMoonById::Positive")
    public void testGetMoonByIdPositive() {
        User user = new User();
        user.setId(1);
        Moon moon = new Moon();
        moon.setId(1);
        moon.setName("moon1");

        Validator<Integer> validator = mock(Validator.class);
        when(validator.get()).thenReturn(1);

        when(ctx.sessionAttribute("user")).thenReturn(user);
        when(ctx.pathParamAsClass("id", Integer.class)).thenReturn(validator);
        when(moonService.getMoonById(user.getId(), 1)).thenReturn(moon);
        when(ctx.json(moon)).thenReturn(ctx);

        moonController.getMoonById(ctx);

        Mockito.verify(ctx).json(moon);
        Mockito.verify(ctx).status(200);
    }

    @Test
    @DisplayName("CreateMoon::Positive")
    public void testCreateMoonPositive() {
        Moon moonInput = new Moon();
        moonInput.setName("moon1");
        Moon moonReturn = new Moon();
        moonReturn.setId(1);
        moonReturn.setName("moon1");

        when(ctx.bodyAsClass(Moon.class)).thenReturn(moonInput);
        when(moonService.createMoon(any(Moon.class))).thenReturn(moonReturn);
        when(ctx.json(moonReturn)).thenReturn(ctx);

        moonController.createMoon(ctx);

        Mockito.verify(ctx).json(moonReturn);
        Mockito.verify(ctx).status(201);
    }

    @Test
    @DisplayName("DeleteMoon::Positive")
    public void testDeleteMoonPositive() {
        Validator<Integer> validator = mock(Validator.class);
        when(validator.get()).thenReturn(1);

        when(ctx.pathParamAsClass("id", Integer.class)).thenReturn(validator);
        when(moonService.deleteMoonById(1)).thenReturn(true);
        when(ctx.json("Moon successfully deleted")).thenReturn(ctx);

        moonController.deleteMoon(ctx);

        Mockito.verify(ctx).json("Moon successfully deleted");
        Mockito.verify(ctx).status(202);
    }

    @Test
    @DisplayName("DeleteMoon::Negative")
    public void testDeleteMoonNegative() {
        Validator<Integer> validator = mock(Validator.class);
        when(validator.get()).thenReturn(1);

        when(ctx.pathParamAsClass("id", Integer.class)).thenReturn(validator);
        when(moonService.deleteMoonById(1)).thenReturn(false);
        when(ctx.result("Failed to delete moon")).thenReturn(ctx);

        moonController.deleteMoon(ctx);

        Mockito.verify(ctx).result("Failed to delete moon");
        Mockito.verify(ctx).status(500);
    }

    @Test
    @DisplayName("GetPlanetMoons::Positive")
    public void testGetPlanetMoonsPositive() {
        List<Moon> moonList = new ArrayList<>();
        Moon moon = new Moon();
        moon.setId(1);
        moon.setName("moon1");
        moonList.add(moon);

        Validator<Integer> validator = mock(Validator.class);
        when(validator.get()).thenReturn(1);

        when(ctx.pathParamAsClass("id", Integer.class)).thenReturn(validator);
        when(moonService.getMoonsFromPlanet(1)).thenReturn(moonList);
        when(ctx.json(moonList)).thenReturn(ctx);

        moonController.getPlanetMoons(ctx);

        Mockito.verify(ctx).json(moonList);
        Mockito.verify(ctx).status(200);
    }
}
