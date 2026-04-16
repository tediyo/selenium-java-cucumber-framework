package com.testfm.steps;

import com.testfm.driver.DriverManager;
import com.testfm.pages.HelpCenterPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;

/**
 * Step definitions for the Help Center flow from Youth Friendly.
 *
 * Tag: @Helpcenter
 * Run: mvn test -Dcucumber.filter.tags="@Helpcenter"
 */
public class HelpCenterSteps {

    private final HelpCenterPage helpCenterPage;

    public HelpCenterSteps() {
        this.helpCenterPage = new HelpCenterPage(DriverManager.getDriver());
    }

    @And("I click the Help Center button")
    public void iClickTheHelpCenterButton() {
        helpCenterPage.clickHelpCenterButton();
        System.out.println("[Step] Clicked Help Center button.");
    }

    @Then("I should see the help center heading {string}")
    public void iShouldSeeTheHelpCenterHeading(String expectedHeading) {
        String actualHeading = helpCenterPage.getHelpCenterHeadingText();
        Assertions.assertEquals(
            expectedHeading,
            actualHeading,
            "Expected Help Center heading did not match."
        );
        System.out.println("[Step] Verified Help Center heading: " + actualHeading);
        pauseSeconds(10);
        System.out.println("[Step] Waited 10 seconds after Help Center verification.");
    }

    private static void pauseSeconds(long seconds) {
        try {
            Thread.sleep(Duration.ofSeconds(seconds).toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

