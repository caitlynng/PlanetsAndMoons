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
    Then the moon name <moonName> should be shown in the Celestial Table

    Examples:
      | planetName   | moonName     |
      | "saturn"     | "phobos"     |
      | "venus"      | "demos"      |


  Scenario Outline: Add Moon - Invalid
    Given the planet name <planetName> exists and displayed in the Celestial Table
    And the Moon option is selected in the location select
    When the user enters <moonName> and planet ID of the <planetName> in the moon add input
    And clicks the submit moon button
    Then an error should be displayed for Moon Adding Error

    Examples:
      | planetName   | moonName                           | errorDescription                                |
      | "SATURN"     | "Phobos"                           | "Moon Already Exists"                           |


  Scenario Outline: Remove Moon - Valid
    Given the moon name <moonName> already exists
    And the Moon option is selected in the location select
    When the user enters moon ID to delete <moonName> in the delete moon input
    And clicks the delete moon button
    Then the alert should be displayed for Moon <moonName> Deleted Successfully

    Examples:
      | moonName |
      | "phobos" |

  Scenario Outline: Remove Moon - Invalid
    Given the moon name "demos" already exists
    And the Moon option is selected in the location select
    When the user enters moon ID to delete <moonName> in the delete moon input
    And clicks the delete moon button
    Then an error should be displayed for Moon Deleting Error

    Examples:
      | moonName |
      | ""       |
      | "-1"     |

  Scenario Outline: Search Moon - Valid
    Given the moon name <moonName> already exists
    When the user enters <moonName> in the search moon input
    And clicks the search moon button
    Then the moon name <moonName> should be shown in the Celestial Table

    Examples:
      | moonName |
      | "demos"  |

  Scenario Outline: Search Moon - Invalid
    When the user enters <moonName> in the search moon input
    And clicks the search moon button
    Then an error should be displayed for No Moon Found Error

    Examples:
      | moonName                   |
      | "NonExistentMoon"          |

