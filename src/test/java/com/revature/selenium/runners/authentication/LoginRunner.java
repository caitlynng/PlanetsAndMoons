package com.revature.selenium.runners.authentication;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/authentication/login.feature",
        glue = "com.revature.selenium.steps.authentication"
)

public class LoginRunner {
}
