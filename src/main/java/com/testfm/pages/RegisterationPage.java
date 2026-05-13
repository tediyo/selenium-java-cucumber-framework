package com.testfm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterationPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final String REGISTER_URL = "https://nycares:teamleader26@test-sfup.newyorkcares.org/user/register";
    private static final String ORIENTATION_URL = "https://test-sfup.newyorkcares.org/orientation-status?check_logged_in=1";

    private final By firstNameField = By.xpath("//*[@id=\"edit-field-name-first-0-value\"]");
    private final By lastNameField = By.xpath("//*[@id=\"edit-field-name-last-0-value\"]");
    private final By phoneField = By.xpath("//*[@id=\"edit-field-phone-number-0-value\"]");
    private final By emailField = By.xpath("//*[@id=\"edit-mail\"]");
    private final By passwordField = By.xpath("//*[@id=\"edit-pass-pass1\"]");
    private final By confirmPasswordField = By.xpath("//*[@id=\"edit-pass-pass2\"]");
    private final By over18Label = By.xpath("//*[@id=\"edit-field-over-18-wrapper\"]/div/label");
    private final By captchaLabel = By.xpath("//*[@id=\"user-register-form\"]/div[6]/div[1]/div/label");
    private final By captchaAnswerField = By.xpath("//*[@id=\"edit-captcha-response\"]");
    private final By registerButton = By.xpath("//*[@id=\"edit-submit\"]");

    private static final Pattern INT_PATTERN = Pattern.compile("(\\d+)");

    public RegisterationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void open() {
        driver.get(REGISTER_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(firstNameField));
    }

    public void enterFirstName(String firstName) {
        type(firstNameField, firstName);
    }

    public void enterLastName(String lastName) {
        type(lastNameField, lastName);
    }

    public void enterPhone(String phone) {
        type(phoneField, phone);
    }

    public void enterEmail(String email) {
        type(emailField, email);
    }

    public void enterPasswordAndConfirm(String password) {
        type(passwordField, password);
        type(confirmPasswordField, password);
    }

    public void checkOver18() {
        WebElement label = wait.until(ExpectedConditions.elementToBeClickable(over18Label));
        try {
            label.click();
        } catch (WebDriverException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", label);
        }
    }

    public int solveAndEnterCaptchaSum() {
        String question = wait.until(ExpectedConditions.visibilityOfElementLocated(captchaLabel)).getText();
        List<Integer> nums = extractInts(question);
        if (nums.size() < 2) {
            throw new IllegalStateException("Could not parse captcha numbers from label: " + question);
        }
        int sum = nums.get(0) + nums.get(1);
        type(captchaAnswerField, String.valueOf(sum));
        return sum;
    }

    public void clickRegister() {
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(registerButton));
        try {
            wait.until(ExpectedConditions.elementToBeClickable(registerButton)).click();
        } catch (WebDriverException firstClickError) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", button);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }
    }

    public boolean isOnOrientationPage() {
        try {
            wait.until(ExpectedConditions.urlToBe(ORIENTATION_URL));
            return true;
        } catch (TimeoutException e) {
            return driver.getCurrentUrl().startsWith(ORIENTATION_URL);
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getOrientationUrl() {
        return ORIENTATION_URL;
    }

    private void type(By locator, String value) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        el.clear();
        el.sendKeys(value);
    }

    private static List<Integer> extractInts(String s) {
        Matcher m = INT_PATTERN.matcher(s);
        List<Integer> nums = new ArrayList<>();
        while (m.find()) {
            nums.add(Integer.parseInt(m.group(1)));
        }
        return nums;
    }
}

