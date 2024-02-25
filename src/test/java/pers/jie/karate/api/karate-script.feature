Feature: Tmall Store
Background:
  * url 'https://tmall.up.railway.app/tmall'

  Scenario: Register
    Given path '/register/doRegister'
    And param user_name = 's5678'
    And param user_password = 'zxc123321'
    And param user_nickname = 'bbb'
    And param user_birthday = '2023-12-05'
    And param user_gender = '0'
    And param user_address = '140581'
    When method post
    Then status 200


  Scenario: Check Login
    Given path '/login/doLogin'
    And param username = 's5678'
    And param password = 'zxc123321'
    When method post
    Then status 200

