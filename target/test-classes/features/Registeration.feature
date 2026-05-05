Feature: New York Cares Registration

  As a new user
  I want to register on the portal
  So that I can access orientation

  @test @RegisterationPage
  Scenario: Register a new user successfully
    Given I navigate to the New York Cares registration page
    When I enter a first name
    And I enter a last name
    And I enter a phone number
    And I enter a unique email
    And I enter a password and confirm it
    And I check the over 18 checkbox
    And I solve the math captcha
    And I click the Register button
    Then I should be redirected to the orientation page
