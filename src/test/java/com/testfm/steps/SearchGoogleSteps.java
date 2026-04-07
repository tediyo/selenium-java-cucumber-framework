package com.testfm.steps;

import com.testfm.driver.DriverManager;
import com.testfm.pages.SearchProjectsPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

/**
 * Step definitions for Search Projects on New York Cares.
 *
 * Tag: @searchProjects
 * Run: mvn test -Dcucumber.filter.tags="@searchProjects"
 */
public class SearchGoogleSteps {

    private final SearchProjectsPage searchProjectsPage;

    public SearchGoogleSteps() {
        this.searchProjectsPage = new SearchProjectsPage(DriverManager.getDriver());
    }

    @Given("I navigate to the New York Cares home page")
    public void iNavigateToTheNewYorkCaresHomePage() {
        searchProjectsPage.openHome();
        System.out.println("[Step] Navigated to New York Cares home page.");
    }

    @When("I click the Search Projects button")
    public void iClickTheSearchProjectsButton() {
        searchProjectsPage.clickSearchProjects();
        System.out.println("[Step] Search Projects clicked.");
    }

    @When("I search for {string} in the fulltext field")
    public void iSearchForInTheFulltextField(String query) {
        searchProjectsPage.searchFulltext(query);
        System.out.println("[Step] Fulltext search submitted: " + query);
    }

    @Then("I select the searched project {string} if it appears in the results")
    public void iSelectTheSearchedProjectIfItAppearsInTheResults(String projectTitle) {
        boolean selected = searchProjectsPage.clickProjectResultContaining(projectTitle);
        Assertions.assertTrue(
            selected,
            "Expected to find and click a result containing: " + projectTitle
                + " — current URL: " + searchProjectsPage.getCurrentUrl()
        );
    }
}
