package com.testfm.steps;

import com.testfm.driver.DriverManager;
import com.testfm.pages.RegisterationPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

public class RegistrationSteps {

    private final RegisterationPage registerationPage;

    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String password;

    public RegistrationSteps() {
        this.registerationPage = new RegisterationPage(DriverManager.getDriver());
    }

    @Given("I navigate to the New York Cares registration page")
    public void iNavigateToTheNewYorkCaresRegistrationPage() {
        generateTestData();
        registerationPage.open();
        System.out.println("[Step] Navigated to registration page.");
    }

    @When("I enter a first name")
    public void iEnterAFirstName() {
        registerationPage.enterFirstName(firstName);
        System.out.println("[Step] First name entered: " + firstName);
    }

    @And("I enter a last name")
    public void iEnterALastName() {
        registerationPage.enterLastName(lastName);
        System.out.println("[Step] Last name entered: " + lastName);
    }

    @And("I enter a phone number")
    public void iEnterAPhoneNumber() {
        registerationPage.enterPhone(phone);
        System.out.println("[Step] Phone entered: " + phone);
    }

    @And("I enter a unique email")
    public void iEnterAUniqueEmail() {
        registerationPage.enterEmail(email);
        System.out.println("[Step] Email entered: " + email);
    }

    @And("I enter a password and confirm it")
    public void iEnterAPasswordAndConfirmIt() {
        registerationPage.enterPasswordAndConfirm(password);
        System.out.println("[Step] Password entered: [PROTECTED]");
    }

    @And("I check the over 18 checkbox")
    public void iCheckTheOver18Checkbox() {
        registerationPage.checkOver18();
        System.out.println("[Step] Checked over-18 checkbox.");
    }

    @And("I solve the math captcha")
    public void iSolveTheMathCaptcha() {
        int sum = registerationPage.solveAndEnterCaptchaSum();
        System.out.println("[Step] Solved captcha sum: " + sum);
    }

    @And("I click the Register button")
    public void iClickTheRegisterButton() {
        pauseSeconds(10);
        registerationPage.clickRegister();
        System.out.println("[Step] Clicked Register button.");
    }

    @Then("I should be redirected to the orientation page")
    public void iShouldBeRedirectedToTheOrientationPage() {
        boolean ok = registerationPage.isOnOrientationPage();
        Assertions.assertTrue(
            ok,
            "Registration FAILED — Expected to land on: "
                + registerationPage.getOrientationUrl()
                + " but current URL is: "
                + registerationPage.getCurrentUrl()
        );
        System.out.println("[Step] Redirected to orientation page: " + registerationPage.getCurrentUrl());
    }

    private void generateTestData() {
        String nonce = String.valueOf(Instant.now().toEpochMilli());
        String rand = randomAlpha(6);

        this.firstName = "Test" + rand;
        this.lastName = "User" + rand;
        this.phone = randomDigits(10);
        this.email = "test." + rand.toLowerCase() + "." + nonce + "@example.com";
        this.password = "Test@" + rand + "Aa1";
    }

    private static void pauseSeconds(long seconds) {
        try {
            Thread.sleep(Duration.ofSeconds(seconds).toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static final SecureRandom RNG = new SecureRandom();

    private static String randomAlpha(int len) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(alphabet.charAt(RNG.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

    private static String randomDigits(int len) {
        String digits = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(digits.charAt(RNG.nextInt(digits.length())));
        }
        return sb.toString();
    }
}

