Feature: Testing a REST API
  Users should be able to submit GET requests to a web service,
  represented by Springboot

  Scenario Outline: Data retrieval from a web service
    #api_S1
    When users want to get information on the "<zone>" sight
    Then the "<zone>" response status should be "<value>"
    Examples:
      | zone | value |
      | 七堵   | 200   |
      | 中山   | 200   |
      | 中正   | 200   |
      | 仁愛   | 200   |
      | 安樂   | 200   |
      | 信義   | 200   |
      | 暖暖   | 200   |
