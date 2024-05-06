eature: Testing Adapt web UI

Scenario: Login Adapt
Given The user is on the Tmall login page
    #session
When the user enters account and password
Then the user will jump to the Tmall homepage

Scenario: Create Guild
Given The user is on the Tmall personal profile page
    #api_S1
When the user enters personal profile
Then the user will refresh to personal profile page
