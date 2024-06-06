package com.revature.selenium.steps.moons;

import com.revature.models.Moon;
import com.revature.models.User;
import com.revature.models.UsernamePasswordAuthentication;
import com.revature.selenium.pages.AuthenticationPage;
import com.revature.selenium.pages.HomePage;
import com.revature.repository.MoonDao;
import com.revature.repository.PlanetDao;
import com.revature.repository.UserDao;
import com.revature.selenium.steps.planets.PlanetStepDefinitions;
import com.revature.service.MoonService;
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

import static com.revature.selenium.utils.DriverUtils.*;
import static org.junit.jupiter.api.Assertions.*;

public class MoonStepDefinitions {
    private WebDriver driver;
    private HomePage homePage;
    private AuthenticationPage authenticationPage;
    private UserDao userDao;
    private PlanetDao planetDao;
    private PlanetService planetService;
    private Moon moon;
    private MoonDao moonDao;
    private MoonService moonService;
    private User testUser;
    private PlanetStepDefinitions planetStepDefinitions;

    @BeforeAll
    public static void beforeSetup() {
        cleanDatabaseTable();
    }

    @Before
    public void setUp() {
        driver = getDriver();
        homePage = new HomePage(driver);
        authenticationPage = new AuthenticationPage(driver);
        planetStepDefinitions = new PlanetStepDefinitions();
    }

    @After
    public void tearDown() {
        quitDriver(driver);
    }

    @Given("the user has an active account")
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

    @And("the user is currently logged in")
    public void theUserIsLoggedIn() {
        driver.get("http://localhost:7001/webpage/login");
        authenticationPage.enterUsername("testuser1");
        authenticationPage.enterPassword("validassword");
        authenticationPage.clickLogin();
        authenticationPage.waitForHomePageLoad();
    }

    @And("the user is on home page")
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

    @Given("the moon name {string} does not already exist")
    public void theMoonNameMoonNameDoesNotAlreadyExist(String moonName) {
        moonDao = new MoonDao();
        moonService = new MoonService(moonDao);
        assertNull(moonService.getMoonByName(testUser.getId(), moonName));
    }

    @And("the planet name {string} exists and displayed in the Celestial Table")
    public void thePlanetNamePlanetNameExistsAndDisplayedInTheCelestialTable(String planetName) throws InterruptedException {
        if (!homePage.isOptionSelected("Planet")) {
            homePage.selectLocationOption("Planet");
        }
        Boolean isPlanetInTable = homePage.isPlanetInTable(planetName.trim().toLowerCase());
        if (!isPlanetInTable) {
            homePage.enterPlanetNameAddInput(planetName);
            homePage.clickSubmitPlanetButton();
        }
//        Thread.sleep(2000);
    }

    @And("the Moon option is selected in the location select")
    public void theMoonOptionIsSelectedInTheLocationSelect() {
        if (!homePage.isOptionSelected("Moon")) {
            homePage.selectLocationOption("Moon");
        }
        String selectedOption = homePage.getSelectedLocationOption();
        assertEquals(selectedOption, "Moon");
    }

    @When("the user enters {string} and planet ID of the {string} in the moon add input")
    public void theUserEntersMoonNameAndPlanetIDOfThePlanetNameInTheMoonAddInput(String moonName, String planetName) {
        int planetID = homePage.getPlanetIdByName(planetName.trim().toLowerCase());
        if (planetID > -1) {
            homePage.enterPlanetIDAddInput(String.valueOf(planetID));
        } else {
            homePage.enterPlanetIDAddInput(planetName);
        }
        homePage.enterMoonNameAddInput(moonName);
    }

    @And("clicks the submit moon button")
    public void clicksTheSubmitMoonButton() throws InterruptedException {
        homePage.clickSubmitMoonButton();
//        Thread.sleep(2000);
    }

    @Then("the moon name {string} should be shown in the Celestial Table")
    public void theMoonNameMoonNameShouldBeAddedSuccessfullyToTheCelestialTable(String moonName) throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
//        Thread.sleep(1000);
        assertTrue(homePage.isPlanetInTable(moonName.trim().toLowerCase()));
    }

    @Then("an error should be displayed for Moon Adding Error")
    public void anErrorShouldBeDisplayedForMoonAddingError() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        String alertText = wait.until(ExpectedConditions.alertIsPresent()).getText();
        assertTrue(alertText.contains("error"), "Error alert not displayed");
        driver.switchTo().alert().accept();
    }

    @Given("the moon name {string} already exists")
    public void theMoonNameMoonNameAlreadyExists(String moonName) {
        assertTrue(homePage.isPlanetInTable(moonName));
    }

    @When("the user enters moon ID to delete {string} in the delete moon input")
    public void theUserEntersMoonIDToDeleteMoonNameInTheDeleteMoonInput(String moonName) {
        int moonId = homePage.getPlanetIdByName(moonName);
        homePage.enterDeleteInput(String.valueOf(moonId));
    }

    @And("clicks the delete moon button")
    public void clicksTheDeleteMoonButton() {
        homePage.clickDeleteButton();
    }

    @Then("the alert should be displayed for Moon {string} Deleted Successfully")
    public void theAlertShouldBeDisplayedForMoonMoonNameDeletedSuccessfully(String moonName) throws InterruptedException {
//        Thread.sleep(2000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        String alertText = wait.until(ExpectedConditions.alertIsPresent()).getText();
        assertTrue(alertText.contains("Deleted moon with ID"), "Delete success alert not displayed");
        driver.switchTo().alert().accept();
        assertFalse(homePage.isPlanetInTable(moonName.trim().toLowerCase()));
    }

    @Then("an error should be displayed for Moon Deleting Error")
    public void anErrorShouldBeDisplayedForMoonDeletingError() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        String alertText = wait.until(ExpectedConditions.alertIsPresent()).getText();
        assertTrue(alertText.contains("Failed"), "Error alert not displayed");
        driver.switchTo().alert().accept();
    }

    @When("the user enters {string} in the search moon input")
    public void theUserEntersMoonNameInTheSearchMoonInput(String moonName) {
        homePage.enterSearchMoonInput(moonName.trim().toLowerCase());
    }

    @And("clicks the search moon button")
    public void clicksTheSearchMoonButton() {
        homePage.clickSearchMoonButton();
    }

    @Then("an error should be displayed for No Moon Found Error")
    public void anErrorShouldBeDisplayedForNoMoonFoundError() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        String alertText = wait.until(ExpectedConditions.alertIsPresent()).getText();
        assertTrue(alertText.contains("No moon found"), "Error alert not displayed");
        driver.switchTo().alert().accept();
    }
}
