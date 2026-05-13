package com.testfm.steps;

import com.testfm.driver.DriverManager;
import com.testfm.pages.UpdateAccountPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;

public class UpdateAccountSteps {

    private final UpdateAccountPage updateAccountPage;
    private static final String SUCCESS_TEXT = "Success:\nYou've updated the user record.";
    private static final String SHORT_SUCCESS_TEXT = "Success:";

    public UpdateAccountSteps() {
        this.updateAccountPage = new UpdateAccountPage(DriverManager.getDriver());
    }

    @And("I click Profile Section")
    public void iClickProfileSection() {
        updateAccountPage.clickProfileSection();
        System.out.println("[Step] Clicked Profile section.");
    }

    @And("I click My Account button")
    public void iClickMyAccountButton() {
        updateAccountPage.clickMyAccountButton();
        System.out.println("[Step] Clicked My Account button.");
    }

    @And("I click Interests Section")
    public void iClickInterestsSection() {
        updateAccountPage.clickInterestsSection();
        System.out.println("[Step] Clicked Interests section.");
    }

    @And("I check Career Prep")
    public void iCheckCareerPrep() {
        updateAccountPage.checkCareerPrep();
        System.out.println("[Step] Career Prep checked.");
    }

    @And("I click Save Interests button")
    public void iClickSaveInterestsButton() {
        updateAccountPage.clickSaveInterestsButton();
        System.out.println("[Step] Clicked Save Interests button.");
    }

    @Then("Career Prep should be checked")
    public void careerPrepShouldBeChecked() {
        Assertions.assertTrue(
            updateAccountPage.isCareerPrepChecked(),
            "Expected Career Prep to be checked."
        );
        System.out.println("[Step] Verified Career Prep is checked.");
    }

    @And("I go to Personal Section")
    public void iGoToPersonalSection() {
        updateAccountPage.clickPersonalSection();
        System.out.println("[Step] Opened Personal section.");
    }

    @And("I update phone number to {string}")
    public void iUpdatePhoneNumberTo(String phoneNumber) {
        updateAccountPage.updateMobilePhoneNumber(phoneNumber);
        System.out.println("[Step] Updated phone number to: " + phoneNumber);
    }

    @And("I click Save Update button in Personal Section")
    public void iClickSaveUpdateButtonInPersonalSection() {
        updateAccountPage.clickSavePersonalUpdateButton();
        System.out.println("[Step] Clicked Save Update button in Personal section.");
    }

    @Then("I should see personal update success message")
    public void iShouldSeePersonalUpdateSuccessMessage() {
        String actual = updateAccountPage.getPersonalUpdateSuccessMessage();
        Assertions.assertTrue(
            isValidSuccessMessage(actual),
            "Expected personal update success message to contain either: " + SUCCESS_TEXT
                + " or " + SHORT_SUCCESS_TEXT + ", but got: " + actual
        );
        System.out.println("[Step] Personal update success message verified.");
    }

    @And("I go to Additional Section")
    public void iGoToAdditionalSection() {
        updateAccountPage.clickAdditionalSection();
        System.out.println("[Step] Opened Additional section.");
    }

    @And("I select Male gender")
    public void iSelectMaleGender() {
        updateAccountPage.selectMaleGender();
        System.out.println("[Step] Selected Male gender.");
    }

    @And("I click Save Update button in Additional Section")
    public void iClickSaveUpdateButtonInAdditionalSection() {
        updateAccountPage.clickSaveAdditionalUpdateButton();
        System.out.println("[Step] Clicked Save Update button in Additional section.");
    }

    @Then("I should see additional update success message")
    public void iShouldSeeAdditionalUpdateSuccessMessage() {
        String actual = updateAccountPage.getAdditionalUpdateSuccessMessage();
        Assertions.assertTrue(
            isValidSuccessMessage(actual),
            "Expected additional update success message to contain either: " + SUCCESS_TEXT
                + " or " + SHORT_SUCCESS_TEXT + ", but got: " + actual
        );
        System.out.println("[Step] Additional update success message verified.");
    }

    private boolean isValidSuccessMessage(String message) {
        if (message == null || message.isBlank()) {
            return false;
        }

        String normalized = message.replace("\r", "").trim();
        return normalized.contains("Success:")
            && (normalized.contains("You've updated the user record.")
                || normalized.equals("Success:"));
    }
}
