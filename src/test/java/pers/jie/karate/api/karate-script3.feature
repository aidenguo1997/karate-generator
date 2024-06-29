Feature: Adapt REST API
  Background:
    * url 'https://api.adapt.chat'
    * configure charset = null
    # 依據Karate規範，後續Scenario如需session，則Scenario: Generate Token (Login)Login to the API with your email and password to retrieve an authentication token.將移入Background
    * path '/login'
    * request {"email":"ab22213395@yahoo.com","password":"zxc123"}
    * method POST
    * status 200
    * def token = response.token
    * header Authorization = token

  Scenario Outline: Create GuildCreates a new guild with the given payload.
    Given path '/guilds'
    And request {"name":"<name>","nonce":"<nonce>"}
    When method POST
    Then status 201
    Examples:
      | name | nonce | 
      | wds | 11296274215075840 | 
      | CRT | 11296275445317632 | 
      | TES | 11296288089571328 | 
      | EYB | 11296294094241792 | 
      | WTR | 11296331297456128 | 
