Feature: Tmall Store
  Background:
    * url 'https://tmall.up.railway.app/tmall'
    #根據Karate規範，如後續Scenario需登入，則Scenario: Login將移入Background
    Given path '/login/doLogin'
    And param username = 's5678'
    And param password = 'zxc123321'
    When method post
    Then status 200

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
