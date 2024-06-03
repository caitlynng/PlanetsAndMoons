package com.revature.controller;

import java.util.List;

import com.revature.models.Moon;
import com.revature.models.User;
import com.revature.service.MoonService;

import io.javalin.http.Context;

public class MoonController {
	
	private MoonService moonService;

	public MoonController(MoonService moonService) {
		this.moonService = moonService;
	}

	public void getAllMoons(Context ctx) {
		User u = ctx.sessionAttribute("user");
		ctx.json(moonService.getAllMoons(u.getId())).status(200);
	}

	public void getMoonByName(Context ctx) {
		User u = ctx.sessionAttribute("user");
		String moonName = ctx.pathParam("name");
		
		Moon m = moonService.getMoonByName(u.getId(), moonName);

		System.out.println(m);

		if (m == null) {
			ctx.json("Moon not found").status(404);
			return;
		}
		
		ctx.json(m).status(200);
	}

	public void getMoonById(Context ctx) {
		try {
			User u = ctx.sessionAttribute("user");
			int moonId = ctx.pathParamAsClass("id", Integer.class).get();

			Moon m = moonService.getMoonById(u.getId(), moonId);

			ctx.json(m).status(200);
		} catch (Exception e) {
			ctx.status(500).json("There was an error");
		}

	}

	public void createMoon(Context ctx) {
		try{
			Moon m = ctx.bodyAsClass(Moon.class);

			Moon outGoingMoon = moonService.createMoon(m);

			ctx.json(outGoingMoon).status(201);
		}
		catch (Exception e) {
			ctx.status(500).json("There was an error");
		}

	}

	public void deleteMoon(Context ctx) {
		// int moonId = ctx.pathParamAsClass("id", Integer.class).get();
		
		// moonService.deleteMoonById(moonId);
		
		// ctx.json("Moon successfully deleted").status(202);
		int moonId = ctx.pathParamAsClass("id", Integer.class).get();
    
		boolean deleted = moonService.deleteMoonById(moonId);
		
		if (deleted) {
			ctx.json("Moon successfully deleted").status(202);
		} else {
			ctx.result("Failed to delete moon").status(500);
		}
	}
	
	public void getPlanetMoons(Context ctx) {

		int planetId = ctx.pathParamAsClass("id", Integer.class).get();

		List<Moon> moonList = moonService.getMoonsFromPlanet(planetId);

		if (moonList.isEmpty()) {
			ctx.json("Moon(s) not found").status(404);
			return;
		}

		ctx.json(moonList).status(200);
	}
}
