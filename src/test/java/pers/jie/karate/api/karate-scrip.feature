Feature: Tmall Store
  Background:
    * url 'https://tmall.up.railway.app/tmall'
    * configure charset = null

  Scenario Outline: Register
    Given path '/register/doRegister'
    And param user_name = '<user_name>'
    And param user_password = '<user_password>'
    And param user_nickname = '<user_nickname>'
    And param user_birthday = '<user_birthday>'
    And param user_gender = '<user_gender>'
    And param user_address = '<user_address>'
    When method POST
    Then status 200
    Examples:
      | user_address | user_birthday | user_gender | user_name | user_nickname | user_password |
      | 140823 | 2024-01-30 | 1 | fg666 | %E8%91%89%E5%A4%A7%E5%93%A5 | zxc123 |
      | 140823 | 2004-02-10 | 1 | xd666 | JJJ | zxc123 |

  Scenario Outline: Check Login
    Given path '/login/doLogin'
    And param username = '<username>'
    And param password = '<password>'
    When method POST
    Then status 200
    Examples:
      | password | username |
      | zxc123 | fg666 |
      | zxc123 | xd666 |
