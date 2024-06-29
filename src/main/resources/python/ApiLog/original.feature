Feature: Product Store
    Background:
    * url 'https://bookstoretest-production.up.railway.app'
    * configure charset = null

  Scenario Outline: Post a list of Product
    Given path '/products'
    And request {"name":"<name>","price":"<price>"}
    When method POST
    Then status 201
    Examples:
      | name | price |
      | 青菜 | 10 |
      | 榴槤 | 800 |

  Scenario Outline: Post a list of Product2
    Given path '/products'
    And request {"name":"<name2>","price":"<price2>"}
    When method POST
    Then status 201
    Examples:
      | name2 | price2 |
      | 青菜 | 10 |
      | 榴槤 | 800 |
      | 白菜 | 60 |
      | 大榴槤 | 820 |

  Scenario Outline: Post a list of Product3
    Given path '/products'
    And request {"name":"<name3>","price":"<price3>"}
    When method POST
    Then status 201
    Examples:
      | name3 | price3 |
      | 青菜 | 10 |
      | 榴槤 | 800 |
      | 白菜 | 60 |
      | 大榴槤 | 820 |
      | 狗大便 | 80 |
      | 豬屎 | 20 |
      | 核桃 | 8200 |

 
