Feature: Sight API
  Background:
    * url 'https://keelungsight-production.up.railway.app'
    * configure charset = null

  Scenario Outline: Get a list of sights
    Given path '/SightAPI'
    And param zone = '<zone>'
    When method GET
    Then status 200
    Examples:
      | zone |
      | 七堵 |
      | 中山 |
      | 中正 |
      | 仁愛 |
      | 安樂 |
      | 信義 |
      | 暖暖 |
