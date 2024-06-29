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
