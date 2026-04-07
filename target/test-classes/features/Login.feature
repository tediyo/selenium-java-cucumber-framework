Feature: New York Cares Login

  As a registered user
  I want to log in to the New York Cares portal
  So that I can access my volunteer dashboard

  @Login
  Scenario: Successful login navigates to the dashboard
    Given I navigate to the New York Cares login page
    When I enter the username "abel.wondwosen+test@newyorkcares.org"
    And I enter the password "Habesha#12"
    And I click the Login button
    Then I should be logged in successfully and redirected to the dashboard page

