Feature: Update Account

  As a logged-in user
  I want to update my account sections
  So that my profile information stays current

  @updateAccount
  Scenario: Update account interests, personal info, and additional details
    Given I navigate to the New York Cares login page
    When I enter the username "abel.wondwosen+test@newyorkcares.org"
    And I enter the password "Habesha#12"
    And I click the Login button
    Then I should be logged in successfully and redirected to the dashboard page
    And I click Profile Section
    And I click My Account button
    And I click Interests Section
    And I check Career Prep
    And I click Save Interests button
    Then Career Prep should be checked
    And I go to Personal Section
    And I update phone number to "323-666-8755"
    And I click Save Update button in Personal Section
    Then I should see personal update success message
    And I go to Additional Section
    And I select Male gender
    And I click Save Update button in Additional Section
    Then I should see additional update success message
