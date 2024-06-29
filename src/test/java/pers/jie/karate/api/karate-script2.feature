Feature: Tmall Store
  Background:
    * url 'https://tmall.up.railway.app/tmall'
    * configure charset = null
    # 依據Karate規範，後續Scenario如需session，則Scenario: Login將移入Background
    * path '/login/doLogin'
    * param username = 'zx123'
    * param password = 'zxc123321'
    * method POST
    * status 200
    * def token = responseHeaders.token
    * header Authorization = token

  Scenario Outline: User Update
    Given path '/user/update'
    And param user_realname = '<user_realname>'
    And param user_password = '<user_password>'
    And param user_nickname = '<user_nickname>'
    And param user_birthday = '<user_birthday>'
    And param user_gender = '<user_gender>'
    And param user_address = '<user_address>'
    When method POST
    Then status 200
    Examples:
      | user_address | user_birthday | user_gender | user_nickname | user_password | user_realname |
      | 110101 | 2024-05-18 | 0 | ee | zxc123321 | zx123 |
      | 110101 | 2024-05-09 | 0 | ww | zxc123321 | qa123 |
