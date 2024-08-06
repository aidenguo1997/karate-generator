Feature: Testing Tmall Store Admin web UI

  Scenario: Login Tmall
    Given The admin is on the Tmall Admin login page
    #session
    When the admin enters account and password
    Then the admin will jump to the Tmall Admin homepage

  Scenario: create product
    Given The admin is on the Create Product page
    #api_S1
    When the admin enters product profile
    Then the admin will refresh to product profile page
