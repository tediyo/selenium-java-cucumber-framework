package com.testfm.steps;

import com.testfm.driver.DriverManager;
import com.testfm.pages.ProjectResourcePage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;

public class ProjectResourceStepes {

    private final ProjectResourcePage projectResourcePage;

    public ProjectResourceStepes() {
        this.projectResourcePage = new ProjectResourcePage(DriverManager.getDriver());
    }

    @And("I click Resources button")
    public void iClickResourcesButton() {
        projectResourcePage.clickResourcesButton();
        System.out.println("[Step] Clicked Resources button.");
    }

    @And("I search resources for {string}")
    public void iSearchResourcesFor(String query) {
        projectResourcePage.searchResources(query);
        System.out.println("[Step] Searched resources for: " + query);
    }

    @Then("I select the resource result for {string} if it appears")
    public void iSelectTheResourceResultForIfItAppears(String title) {
        boolean selected = projectResourcePage.selectResultContaining(title);
        Assertions.assertTrue(
            selected,
            "Expected to find and select resource result: " + title
                + " but no result appeared. Current URL: " + projectResourcePage.getCurrentUrl()
        );
        System.out.println("[Step] Selected resource result for: " + title);
    }
}
