Feature: Adapt REST API
  Background:
    * url 'https://api.adapt.chat'
    * configure charset = null
    # 依據Karate規範，後續Scenario如需session，則Scenario: Generate Token (Login)Login to the API with your email and password to retrieve an authentication token.將移入Background
    * path '/login'
    * request {"email":"ab295@gmail.com","password":"6666666"}
    * method POST
    * status 200
    * def token = response.token
    * header Authorization = token

  Scenario: Create GuildCreates a new guild with the given payload.
    Given path '/guilds'
    And request {"name":"aiden777","nonce":"10666230188605440"}
    When method POST
    Then status 201
