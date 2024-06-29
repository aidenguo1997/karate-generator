Feature: Product Store

  Scenario: Post product
    Given the user is on the product management page
    #api_S1
    When the user enters name and price to creates a new product
    Then the new product should be added to the product list
    And the product details should be accurate

  Scenario: Put product
    Given the user is on the product management page
    #api_S2
    When the user selects a product price to update
    And modifies the product details
    And confirms the update
    Then the product should be updated in the product list
    And the product details should be accurate

  Scenario: Delete product
    Given the user is on the product management page
    #api_S3
    When the user selects a product to delete
    And confirms the deletion
    Then the product should be deleted
    And the product list should be updated
    