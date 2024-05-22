package com.revature.selenium.runners.moons;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/moons/moon.feature",
        glue = "com.revature.selenium.steps.moons",
        plugin = {
                "pretty",
                "html:src/test/resources/reports/moon.html"}
)

public class MoonRunner {
}
