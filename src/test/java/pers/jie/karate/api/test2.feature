Feature: JWT REST API
  Background:
    * url 'http://127.0.0.1:8102'
    * configure charset = null
    * path '/auth/login'
    * request {"username":"jie123","password":"??????"}
    * method POST
    * status 200
    * def token = responseHeaders.token
    * header Authorization = token

  Scenario: Create tasks
    Given path '/tasks'
    And path taskId = '0'
    When method DELETE
    Then status 200
