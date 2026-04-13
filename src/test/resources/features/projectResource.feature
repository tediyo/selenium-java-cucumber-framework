Feature: Project Resource

  As a logged-in user
  I want to search a project resource
  So that I can open the matching resource entry

  @projectResource
  Scenario: Open a project resource from Resources search
    Given I navigate to the New York Cares login page
    When I enter the username "abel.wondwosen+test@newyorkcares.org"
    And I enter the password "Habesha#12"
    And I click the Login button
    Then I should be logged in successfully and redirected to the dashboard page
    And I click Resources button
    And I search resources for "Addressing Project Emergencies"
    Then I select the resource result for "Addressing Project Emergencies" if it appears
