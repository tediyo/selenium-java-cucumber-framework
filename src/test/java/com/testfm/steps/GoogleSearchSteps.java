package com.testfm.steps;

import com.testfm.driver.DriverManager;
import com.testfm.pages.GoogleSearchPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

/**
 * Step Definitions for Google Search feature.
 * Maps each Gherkin step to its Java implementation.
 */
public class GoogleSearchSteps {

    private final GoogleSearchPage googlePage;

    public GoogleSearchSteps() {
        this.googlePage = new GoogleSearchPage(DriverManager.getDriver());
    }

    @Given("I open the Google homepage")
    public void iOpenTheGoogleHomepage() {
        googlePage.open();
        System.out.println("[Step] Opened Google homepage.");
    }

    @When("I search for {string}")
    public void iSearchFor(String searchTerm) {
        googlePage.searchFor(searchTerm);
        System.out.println("[Step] Searched for: " + searchTerm);
    }

    @Then("the page title should contain {string}")
    public void thePageTitleShouldContain(String expected) {
        String actualTitle = googlePage.getPageTitle();
        System.out.println("[Step] Page title: " + actualTitle);
        Assertions.assertTrue(
            actualTitle.toLowerCase().contains(expected.toLowerCase()),
            "Expected page title to contain '" + expected + "' but got: '" + actualTitle + "'"
        );
    }

    @Then("search results should be displayed")
    public void searchResultsShouldBeDisplayed() {
        boolean displayed = googlePage.areResultsDisplayed();
        System.out.println("[Step] Results displayed: " + displayed);
        Assertions.assertTrue(displayed, "Search results were NOT displayed on the page.");
    }
}
