Feature: New York Cares Login

  As a registered user
  I want to log in to the New York Cares portal
  So that I can access my volunteer dashboard

  @Login
  Scenario Outline: Login attempts (multiple credentials)
    Given I navigate to the New York Cares login page
    When I enter the username "<username>"
    And I enter the password "<password>"
    And I click the Login button
    Then I should be logged in successfully and redirected to the dashboard page

    Examples:
      | username                          | password     |
      | abel.wondwosen+test@newyorkcares.org | 12345678    |
      | abel.wondwosen+test@newyorkcares.org | Habesha#12  |
      #| wrong.user@newyorkcares.org       | WrongPass!1  |
      #| test.user+1@newyorkcares.org      | Habesha#12   |
      #| not-an-email                      | Habesha#12   |


