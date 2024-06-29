Feature: Testing Adapt web UI

  Scenario: Login Adapt
    Given The user is on the Adapt login page
    #session
    When the user enters account and password
    Then the user will jump to the Adapt homepage

  Scenario: Create Guild
    Given The user is on the Adapt Guild profile page
    #api_S1
    When the user enters Guild profile
    Then the user will refresh to Guild profile page
