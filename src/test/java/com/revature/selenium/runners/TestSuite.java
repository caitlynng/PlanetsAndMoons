package com.revature.selenium.runners;

import com.revature.selenium.runners.authentication.LoginRunner;
import com.revature.selenium.runners.authentication.RegisterRunner;
import com.revature.selenium.runners.moons.MoonRunner;
import com.revature.selenium.runners.planets.PlanetRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoginRunner.class,
        RegisterRunner.class,
        PlanetRunner.class,
        MoonRunner.class
})
public class TestSuite {
    // This class remains empty
    // Used only as a holder for the above annotations
}
