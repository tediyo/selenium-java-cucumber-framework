Feature: Google Search

  As a user
  I want to search for something on Google
  So that I can find relevant results

  @smoke @search
  Scenario: Search for Selenium on Google
    Given I open the Google homepage
    When I search for "Selenium WebDriver"
    Then the page title should contain "Selenium WebDriver"
    And search results should be displayed

 