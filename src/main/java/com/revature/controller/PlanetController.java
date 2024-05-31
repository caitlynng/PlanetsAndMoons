package com.revature.controller;

import com.revature.models.Planet;
import com.revature.models.User;
import com.revature.service.PlanetService;

import io.javalin.http.Context;

public class PlanetController {

    private PlanetService planetService;

    public PlanetController(PlanetService planetService) {
        this.planetService = planetService;
    }

    public void getAllPlanets(Context ctx) {
        try {
            User u = ctx.sessionAttribute("user");
            ctx.json(planetService.getAllPlanets(u.getId())).status(200);
        } catch (Exception e) {
            ctx.json("There was an error").status(500);
        }
    }


    public void getPlanetByName(Context ctx) {
        try {
            User u = ctx.sessionAttribute("user");
            String planetName = ctx.pathParam("name");

            if (planetName.isEmpty()) {
                ctx.json("Planet name cannot be empty").status(400);
                return;
            }

            Planet planet = planetService.getPlanetByName(u.getId(), planetName);

            if (planet == null) {
                ctx.json("Planet not found").status(404);
                return;
            }

            ctx.json(planet).status(200);
        } catch (Exception e) {
            ctx.json("There was an error").status(500);
        }
    }

    public void getPlanetByID(Context ctx) {
        try {
            User u = ctx.sessionAttribute("user");
            int planetId = ctx.pathParamAsClass("id", Integer.class).get();

            Planet p = planetService.getPlanetById(u.getId(), planetId);

            ctx.json(p).status(200);
        } catch (Exception e) {
            ctx.status(500).json("There was an error");
        }
    }

    public void createPlanet(Context ctx) {
        try {
            Planet planetToBeCreated = ctx.bodyAsClass(Planet.class);
            User u = ctx.sessionAttribute("user");

            Planet createdPlanet = planetService.createPlanet(u.getId(), planetToBeCreated);

            ctx.json(createdPlanet).status(201);
        } catch (Exception e) {
            ctx.status(500).json("There was an error");
        }
    }


    public void deletePlanet(Context ctx) {
        try {
            User u = ctx.sessionAttribute("user");
            int planetId = ctx.pathParamAsClass("id", Integer.class).get();

            boolean deleted = planetService.deletePlanetById(u.getId(), planetId);

            if (deleted) {
                ctx.json("Planet successfully deleted").status(202);
            } else {
                ctx.json("Failed to delete planet").status(500);
            }
        } catch (Exception e) {
            ctx.status(500).json("There was an error");
        }
    }
}
