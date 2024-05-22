package com.revature.selenium.runners.planets;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/planets/planet.feature",
        glue = "com.revature.selenium.steps.planets",
        plugin = {
                "pretty",
                "html:src/test/resources/reports/planets.html"}
)

public class PlanetRunner {
}
