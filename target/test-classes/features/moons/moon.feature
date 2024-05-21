Feature:Moon Management

  Background:
    Given the user has an active account
    And the user is currently logged in
    And the user is on home page

  Scenario Outline: Add Moon - Valid
    Given the moon name <moonName> does not already exist
    And the planet name <planetName> exists and displayed in the Celestial Table
    And the Moon option is selected in the location select
    When the user enters <moonName> and planet ID of the <planetName> in the moon add input
    And clicks the submit moon button
    Then the moon name <moonName> should be added successfully to the Celestial Table

    Examples:
      | planetName   | moonName     |
      | "SATURN"     | "Phobos"     |
      | "SATURN"     | "titan"      |
      | "  Venus  "  | "  Demos   " |
      | "MARS123"    | "UROPA"      |
      | "alpha@beta" | "moon-777!"  |

  Scenario Outline: Add Moon - Invalid
    Given the planet name <planetName> exists and displayed in the Celestial Table
    And the Moon option is selected in the location select
    When the user enters <moonName> and planet ID of the <planetName> in the moon add input
    And clicks the submit moon button
    Then an error should be displayed for Moon Adding Error

    Examples:
      | planetName    | moonName                           | errorDescription                                |
      | "SATURN"      | "Phobos"                           | "Moon Already Exists"                           |
      | "SATURN"      | ""                                 | "Empty Moon Name"                               |
      | "Venus"       | "averylongmoonnamewaytoobig123456" | "Moon Name Too Long"                            |
      | "MARS123"     | "ムーン"                              | "Non-ASCII Moon Name"                           |
      | "alpha@beta"  | "moon'; DROP TABLE moons;"         | "Moon Name Containing SQL Injection Characters" |
      | "NonExistent" | "NewMoon"                          | "Non-existing Planet ID"                        |

