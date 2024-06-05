package com.revature.service;

import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;
import com.revature.exceptions.MoonFailException;
import com.revature.exceptions.PlanetFailException;
import com.revature.models.Planet;
import com.revature.repository.PlanetDao;

public class PlanetService {

    private PlanetDao dao;

    public PlanetService(PlanetDao dao) {
        this.dao = dao;
    }

    public List<Planet> getAllPlanets(int ownerId) {
        // TODO Auto-generated method stub
        return dao.getAllPlanets(ownerId);
    }

    public Planet getPlanetByName(int ownerId, String planetName) {
        // TODO Auto-generated method stub
        // Trim leading and trailing spaces from the planet name
        String trimmedName = planetName.trim();

        // Convert the planet name to lowercase
        String lowercaseName = trimmedName.toLowerCase();
//        System.out.println(lowercaseName);
        return dao.getPlanetByName(ownerId, lowercaseName);
    }

    public Planet getPlanetById(int ownerId, int planetId) {
        // TODO Auto-generated method stub
        return dao.getPlanetById(ownerId, planetId);
    }

    public Planet createPlanet(int ownerId, Planet planet) {
        // TODO Auto-generated method stub

        //Check if ownerId is missing
        if (ownerId <= 0) {
            return null;
        }
        // Check if the planet name is null or empty
        if (planet.getName() == null || planet.getName().isEmpty()) {
            throw new PlanetFailException("Planet name cannot be null or empty");
        }

        // Trim leading and trailing spaces from the planet name
        String trimmedName = planet.getName().trim();

        // Convert the planet name to lowercase
        String lowercaseName = trimmedName.toLowerCase();

        // Check if the lowercase name contains non-ASCII characters
        if (!isAllAscii(lowercaseName)) {
            return null;
        }

        // Check for SQL injection patterns in the name
        if (containsSQLInjection(lowercaseName)) {
            return null;
        }

        // Check if the length of the name after trimming and lowercase conversion exceeds 30 characters
        if (lowercaseName.length() > 30) {
            return null;
        }

        // Check if a planet with the same name already exists for the given ownerId
        Planet existingPlanet = dao.getPlanetByName(ownerId, lowercaseName);
        if (existingPlanet != null) {
            return null;
        }

        // Set the ownerId to the planet
        planet.setOwnerId(ownerId);

        // Set the trimmed and lowercase name to the planet
        planet.setName(lowercaseName);

        // Create the planet
        return dao.createPlanet(planet);
    }

    public boolean deletePlanetById(int ownerId, int planetId) {
        // TODO Auto-generated method stub

        //check if planetId or ownerId is missing
        if (planetId <= 0 || ownerId <= 0) {
            return false;
        }

        // Check if the planet exists for the given ownerId and planetId
        Planet existingPlanet = getPlanetById(ownerId, planetId);
        if (existingPlanet == null) {
            return false;
        }

        return dao.deletePlanetById(ownerId, planetId);
    }


    // Function to check if a string contains SQL injection patterns
    private boolean isAllAscii(String str) {
        // Validate input for ASCII characters and specific pattern
        CharMatcher asciiMatcher = CharMatcher.ascii();
        return asciiMatcher.matchesAllOf(str);
    }

    private boolean containsSQLInjection(String str) {
        Pattern sqlInjectionPattern = Pattern.compile("(?i)(SELECT|INSERT|UPDATE|DELETE|DROP|ALTER).*");
        return sqlInjectionPattern.matcher(str).find();
    }

}
