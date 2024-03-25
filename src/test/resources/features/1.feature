Feature: Testing Tmall Store web UI

  Scenario: Login Tmall
    Given The user is on the Tmall login page
    #api_S1
    When the user enters account and password
    Then the user will jump to the Tmall homepage

  Scenario: Register Tmall account
    Given the user is on the Tmall register page
    #api_S2
    When the user enters personal data
    Then the user will jump to the Tmall login page
