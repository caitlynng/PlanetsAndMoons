package com.revature.selenium.steps.planets;

import com.revature.models.Planet;
import com.revature.models.User;
import com.revature.models.UsernamePasswordAuthentication;
import com.revature.selenium.pages.AuthenticationPage;
import com.revature.selenium.pages.HomePage;
import com.revature.repository.PlanetDao;
import com.revature.repository.UserDao;
import com.revature.service.PlanetService;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static com.revature.selenium.utils.DriverUtils.*;

public class PlanetStepDefinitions {
    private WebDriver driver;
    private HomePage homePage;
    private AuthenticationPage authenticationPage;
    private UserDao userDao;
    private PlanetDao planetDao;
    private PlanetService planetService;
    private User testUser;

    @BeforeAll
    public static void beforeSetup() {
        cleanDatabaseTable();
    }

    @Before
    public void setUp() {
        driver = getDriver();
        homePage = new HomePage(driver);
        authenticationPage = new AuthenticationPage(driver);
    }

    @After
    public void tearDown() {
        quitDriver(driver);
    }

    @Given("the user has an existing account")
    public void theUserHasAnExistingAccount() {
        userDao = new UserDao();
        testUser = new User();

        String username = "testuser1";
        String password = "validassword";
        UsernamePasswordAuthentication user = new UsernamePasswordAuthentication();
        user.setUsername(username);
        user.setPassword(password);

        if (userDao.getUserByUsername(username) == null) {
            testUser = userDao.createUser(user);
            assertNotNull(user);
            assertEquals(username, testUser.getUsername());
        }
    }

    @And("the user is logged in")
    public void theUserIsLoggedIn() {
        driver.get("http://localhost:7001/webpage/login");
        authenticationPage.enterUsername("testuser1");
        authenticationPage.enterPassword("validassword");
        authenticationPage.clickLogin();
        authenticationPage.waitForHomePageLoad();
    }

    @And("the user is on the home page")
    public void theUserIsOnTheHomePage() {
        String pageTitle = driver.getTitle();
        assertEquals("Home", pageTitle, "User is not directed to the Home page");

        // Check if ownerID is present in sessionStorage
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String ownerId = (String) js.executeScript("return sessionStorage.getItem('userId');");
        String userName = (String) js.executeScript("return sessionStorage.getItem('user');");
        assertTrue(ownerId != null && !ownerId.isEmpty(), "ownerID is not present in sessionStorage");
        assertEquals("testuser1", userName);

        testUser.setId(Integer.parseInt(ownerId));
        testUser.setUsername(userName);
    }

    @Given("the planet name {string} does not already exist")
    public void thePlanetDoesNotAlreadyExist(String planetName) {
        planetDao = new PlanetDao();
        planetService = new PlanetService(planetDao);
        assertNull(planetService.getPlanetByName(testUser.getId(), planetName));
    }

    @And("the Planet option is selected in the location select")
    public void thePlanetOptionIsSelectedInTheLocationSelect() {
        if (!homePage.isOptionSelected("Planet")) {
            homePage.selectLocationOption("Planet");
        }
        String selectedOption = homePage.getSelectedLocationOption();
        assertEquals(selectedOption, "Planet");
    }

    @When("the user enters {string} in the planet add input")
    public void theUserEntersInThePlanetInput(String planetName) {
        homePage.enterPlanetNameAddInput(planetName);
    }

    @And("clicks the submit planet button")
    public void clicksTheSubmitPlanetButton() {
        homePage.clickSubmitPlanetButton();
    }

    @Then("the planet name {string} should be added successfully to the Celestial Table")
    public void thePlanetShouldBeAddedSuccessfullyToTheCelestialTable(String planetName) throws InterruptedException {
        Thread.sleep(500);
        planetDao = new PlanetDao();
        planetService = new PlanetService(planetDao);
        Planet planet = planetService.getPlanetByName(testUser.getId(), planetName);

        // Assert that the method returns a planet
        assertNotNull(planet, "Planet should be found by the service");

        // Query the table to check for the matching row
        boolean isPlanetInTable = homePage.isPlanetInTable(planet.getName());

        // Assert that the planet is found in the table
        assertTrue(isPlanetInTable, "Planet should be found in the celestial table");
    }


    @Given("the planet name {string} already exists")
    public void thePlanetNameAlreadyExists(String planetName) {
        assertTrue(homePage.isPlanetInTable(planetName.trim().toLowerCase()));
    }


    @When("the user enters planet ID to delete {string} in the delete planet input")
    public void theUserEntersPlanetIDInTheDeletePlanetInput(String planetName) {
        int planetID = homePage.getPlanetIdByName(planetName);
        planetDao = new PlanetDao();
        planetService = new PlanetService(planetDao);
        homePage.enterDeleteInput(String.valueOf(planetID));
    }

    @And("clicks the delete button")
    public void clicksTheDeleteButton() throws InterruptedException {
        Thread.sleep(2000);
        homePage.clickDeleteButton();
    }

    @Then("the alert should be displayed for Planet {string} Deleted Successfully")
    public void theAlertShouldBeDisplayedForPlanetDeleteSuccess(String planetName) throws InterruptedException {
        Thread.sleep(2000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        String alertText = wait.until(ExpectedConditions.alertIsPresent()).getText();
        assertTrue(alertText.contains("Deleted planet with ID"), "Delete success alert not displayed");
        driver.switchTo().alert().accept();
        assertFalse(homePage.isPlanetInTable(planetName.trim().toLowerCase()));
    }

    @Then("the Error alert should be displayed")
    public void theErrorAlertShouldBeDisplayed() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        String alertText = wait.until(ExpectedConditions.alertIsPresent()).getText();
        assertTrue(alertText.contains("Failed"), "Error alert not displayed");
        driver.switchTo().alert().accept();
    }

    @When("the user enters {string} in the delete planet input")
    public void theUserEntersInTheDeletePlanetInput(String planetName) {
        homePage.enterDeleteInput(planetName.trim().toLowerCase());
    }

    @And("clicks the search planet button")
    public void clicksTheSearchPlanetButton() {
        homePage.clickSearchPlanetButton();
    }

    @When("the user enters {string} in the search planet input")
    public void theUserEntersPlanetNameInTheSearchPlanetInput(String planetName) {
        homePage.enterSearchPlanetInput(planetName.trim().toLowerCase());
    }

    @Then("the celestial table displays the {string}")
    public void theCelestialTableDisplaysThePlanetName(String planetName) {
        assertTrue(homePage.isPlanetInTable(planetName.trim().toLowerCase()));
    }

    @And("the user enters planet ID instead of {string} in the search planet input")
    public void theUserEntersPlanetIDInsteadOfInTheSearchPlanetInput(String planetName) {
        int planetID = homePage.getPlanetIdByName(planetName);
        homePage.enterSearchPlanetInput(String.valueOf(planetID));
    }
}
