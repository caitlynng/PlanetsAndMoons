Feature:Planet Management

  Background:
    Given the user has an existing account
    And the user is logged in
    And the user is on the home page

  Scenario Outline: Add Planet - Valid
    Given the planet name <planetName> does not already exist
    And the Planet option is selected in the location select
    When the user enters <planetName> in the planet add input
    And clicks the submit planet button
    Then the planet name <planetName> should be added successfully to the Celestial Table

    Examples:
      | planetName   | Description                          |
      | "earth"      | all lowercase name                   |
      | "Jupiter"    | one upper case name                  |

  Scenario Outline: Add Planet - Invalid
    Given the Planet option is selected in the location select
    When the user enters <planetName> in the planet add input
    And clicks the submit planet button
    Then the Error alert should be displayed

    Examples: Negative Cases
      | planetName                                      |  errorDescription                          |
      | ""                                              |  empty string                              |
      | "AstroAdventureWonderlandWonderlandWonderland " |  name is too long                          |


  Scenario: Add Planet - Planet Already Exists
    Given the planet name "earth" already exists
    And the Planet option is selected in the location select
    When the user enters "earth" in the planet add input
    And clicks the submit planet button
    Then  the Error alert should be displayed


  Scenario Outline: Remove Planet - Valid
    Given the planet name <planetName> already exists
    And the Planet option is selected in the location select
    When the user enters planet ID to delete <planetName> in the delete planet input
    And clicks the delete button
    Then the alert should be displayed for Planet <planetName> Deleted Successfully

    Examples:
      | planetName |
      | "earth"    |

  Scenario Outline: Remove Planet - Invalid
    Given the planet name "jupiter" already exists
    And the Planet option is selected in the location select
    When the user enters <planetName> in the delete planet input
    And clicks the delete button
    Then the Error alert should be displayed

    Examples:
      | planetName   |
      | "jupiter"     |

  Scenario Outline: Search Planet - Valid
    Given the planet name <planetName> already exists
    When the user enters <planetName> in the search planet input
    And clicks the search planet button
    Then the celestial table displays the <planetName>

    Examples:
      | planetName   |
      | "SATURN"     |
  Scenario Outline: Search Planet - Invalid
    When the user enters <planetName> in the search planet input
    And clicks the search planet button
    Then the Error alert should be displayed

    Examples:
      | planetName                     | errorDescription                          |
      | "NonExistentPlanet"            | planet name doesn't exist                 |
