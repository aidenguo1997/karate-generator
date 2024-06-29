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
      | 榴槤 | 10 |
      | 榴槤 | 800 |
      | 青菜 | 10 |
      | 青菜 | 800 |

  Scenario Outline: Post a list of Product2
    Given path '/products'
    And request {"name":"<name2>","price":"<price2>"}
    When method POST
    Then status 201
    Examples:
      | name2 | price2 |
      | 榴槤 | 10 |
      | 榴槤 | 820 |
      | 榴槤 | 800 |
      | 榴槤 | 60 |
      | 白菜 | 10 |
      | 白菜 | 820 |
      | 白菜 | 800 |
      | 白菜 | 60 |
      | 大榴槤 | 10 |
      | 大榴槤 | 820 |
      | 大榴槤 | 800 |
      | 大榴槤 | 60 |
      | 青菜 | 10 |
      | 青菜 | 820 |
      | 青菜 | 800 |
      | 青菜 | 60 |

  Scenario Outline: Post a list of Product3
    Given path '/products'
    And request {"name":"<name3>","price":"<price3>"}
    When method POST
    Then status 201
    Examples:
      | name3 | price3 |
      | 榴槤 | 800 |
      | 榴槤 | 80 |
      | 榴槤 | 8200 |
      | 榴槤 | 20 |
      | 榴槤 | 10 |
      | 榴槤 | 60 |
      | 榴槤 | 820 |
      | 核桃 | 800 |
      | 核桃 | 80 |
      | 核桃 | 8200 |
      | 核桃 | 20 |
      | 核桃 | 10 |
      | 核桃 | 60 |
      | 核桃 | 820 |
      | 豬屎 | 800 |
      | 豬屎 | 80 |
      | 豬屎 | 8200 |
      | 豬屎 | 20 |
      | 豬屎 | 10 |
      | 豬屎 | 60 |
      | 豬屎 | 820 |
      | 青菜 | 800 |
      | 青菜 | 80 |
      | 青菜 | 8200 |
      | 青菜 | 20 |
      | 青菜 | 10 |
      | 青菜 | 60 |
      | 青菜 | 820 |
      | 狗大便 | 800 |
      | 狗大便 | 80 |
      | 狗大便 | 8200 |
      | 狗大便 | 20 |
      | 狗大便 | 10 |
      | 狗大便 | 60 |
      | 狗大便 | 820 |
      | 白菜 | 800 |
      | 白菜 | 80 |
      | 白菜 | 8200 |
      | 白菜 | 20 |
      | 白菜 | 10 |
      | 白菜 | 60 |
      | 白菜 | 820 |
      | 大榴槤 | 800 |
      | 大榴槤 | 80 |
      | 大榴槤 | 8200 |
      | 大榴槤 | 20 |
      | 大榴槤 | 10 |
      | 大榴槤 | 60 |
      | 大榴槤 | 820 |

 
