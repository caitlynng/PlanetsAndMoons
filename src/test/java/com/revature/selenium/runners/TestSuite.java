package com.revature.selenium.runners;
import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {
                "src/test/resources/features/authentication/login.feature",
                "src/test/resources/features/authentication/register.feature",
                "src/test/resources/features/moons/moon.feature",
                "src/test/resources/features/planets/planet.feature"

        },
        glue = {
                "com.revature.selenium.steps.authentication",
                "com.revature.selenium.steps.moons",
                "com.revature.selenium.steps.planets",
        },
        plugin = {
                "pretty",
                "html:target/reports/test-suite.html"
        }
)
public class TestSuite {
    // This class remains empty
    // Used only as a holder for the above annotations
}
