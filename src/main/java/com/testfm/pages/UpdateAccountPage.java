package com.testfm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class UpdateAccountPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By profileSectionButton = By.xpath("//*[@id='block-teamleaders-account-menu']/div/button/div");
    private final By myAccountButton = By.xpath("//*[@id='block-teamleaders-account-menu']/div/div/ul/li[3]/a");
    private final By interestsSectionTab = By.xpath("//*[@id='tab1']");
    private final By careerPrepCheckbox = By.xpath("//*[@id='interests-form']/fieldset/div[1]/div[3]");
    private final By saveInterestsButton = By.xpath("//*[@id='interests-form']/div/button");

    private final By personalSectionTab = By.xpath("//*[@id='tab2']");
    private final By mobilePhoneField = By.xpath("//*[@id='MobilePhone']");
    private final By savePersonalUpdateButton = By.xpath("//*[@id='general']/div/div/form/button");
    private final By personalSuccessMessage = By.xpath("//*[@id='tlMainContent']/div/div[1]/div/div[3]/div[1]/div/div/div/div");

    private final By additionalSectionTab = By.xpath("//*[@id='tab3']");
    private final By maleGenderRadio = By.xpath("//*[@id='additional']/div/div/form/div[1]/div/label[2]/input");
    private final By saveAdditionalUpdateButton = By.xpath("//*[@id='additional']/div/div/form/button/span");
    private final By additionalSuccessMessage = By.xpath("//*[@id='tlMainContent']/div/div[1]/div/div[3]/div[1]/div/div");

    public UpdateAccountPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void clickProfileSection() {
        click(profileSectionButton);
    }

    public void clickMyAccountButton() {
        click(myAccountButton);
    }

    public void clickInterestsSection() {
        click(interestsSectionTab);
    }

    public void checkCareerPrep() {
        if (!isCareerPrepChecked()) {
            click(careerPrepCheckbox);
        }
    }

    public void clickSaveInterestsButton() {
        click(saveInterestsButton);
    }

    public boolean isCareerPrepChecked() {
        WebElement container = wait.until(ExpectedConditions.presenceOfElementLocated(careerPrepCheckbox));
        if (isElementChecked(container)) {
            return true;
        }

        for (WebElement input : container.findElements(By.xpath(".//input"))) {
            if (isElementChecked(input)) {
                return true;
            }
        }

        String containerClass = container.getDomAttribute("class");
        return containerClass != null && containerClass.toLowerCase().contains("checked");
    }

    public void clickPersonalSection() {
        click(personalSectionTab);
    }

    public void updateMobilePhoneNumber(String phoneNumber) {
        WebElement field = wait.until(ExpectedConditions.elementToBeClickable(mobilePhoneField));
        field.clear();
        field.sendKeys(phoneNumber);
    }

    public void clickSavePersonalUpdateButton() {
        click(savePersonalUpdateButton);
    }

    public String getPersonalUpdateSuccessMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(personalSuccessMessage)).getText().trim();
    }

    public void clickAdditionalSection() {
        click(additionalSectionTab);
    }

    public void selectMaleGender() {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(maleGenderRadio));
        if (!isElementChecked(element)) {
            click(maleGenderRadio);
        }
    }

    public void clickSaveAdditionalUpdateButton() {
        click(saveAdditionalUpdateButton);
    }

    public String getAdditionalUpdateSuccessMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(additionalSuccessMessage)).getText().trim();
    }

    private void click(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
        } catch (ElementClickInterceptedException | TimeoutException e) {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private boolean isElementChecked(WebElement element) {
        String ariaChecked = element.getDomAttribute("aria-checked");
        String checked = element.getDomAttribute("checked");
        String selected = element.getDomAttribute("selected");
        return element.isSelected()
            || "true".equalsIgnoreCase(ariaChecked)
            || checked != null
            || selected != null;
    }
}
