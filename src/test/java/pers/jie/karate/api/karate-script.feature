Feature: Tmall Store
  Background:
    * url 'https://tmall.up.railway.app/tmall'
    #依據Karate規範，後續Scenario如需session，則Scenario: Login將移入Background
    * path '/login/doLogin'
    * param username = 's5678'
    * param password = 'zxc123321'
    * method post
    * status 200
    * def token = responseHeaders.token
    * header Authorization = token

  Scenario: User Update
    Given path '/user/update'
    And param user_nickname = 'Alean'
    And param user_realname = 'Guo+Jie'
    And param user_password = 'zxc123321'
    And param user_password_one = 'zxc123321'
    And param user_gender = '1'
    And param user_birthday = '2023-11-29'
    And param user_address = '220421'
    When method post
    Then status 200
