Feature: Tmall Store

  Scenario: Register
    Given url 'http://localhost:8080/tmall'
    And path '/register/doRegister'
    And request {"user_name":"z123","user_password":"cd069798","user_nickname":"bbb","user_birthday":"2023-12-05","user_gender":"0","user_address":"140581"}
    When method post
    Then status 200


  Scenario: Check Login
    Given url 'http://localhost:8080/tmall'
    And path '/login/doLogin'
    And request {"username":"z123","password":"cd069798"}
    When method post
    Then status 200

