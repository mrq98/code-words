Feature: Start Game

  Scenario: Create new game without username - EASY
    Given url baseUrl + '/game'
    And request { "difficulty": "EASY" }
    When method POST
    Then status 200
    And match response.id != null
    And match response.remainingAttempts == 6
    And match response.status == "IN_PROGRESS"
    And assert response.maskedWord.length >= 7  /* length is (2n - 1) of the expectation since, we add spaces to mask characters and last character has no space at the end*/
    And assert response.maskedWord.length <= 11

  Scenario: Create new game without username - NORMAL
    Given url baseUrl + '/game'
    And request { "difficulty": "NORMAL" }
    When method POST
    Then status 200
    And match response.id != null
    And match response.remainingAttempts == 7
    And match response.status == "IN_PROGRESS"
    And assert response.maskedWord.length >= 13  /* length is (2n - 1) of the expectation since, we add spaces to mask characters and last character has no space at the end*/
    And assert response.maskedWord.length <= 17

  Scenario: Create new game without username - HARD
    Given url baseUrl + '/game'
    And request { "difficulty": "HARD" }
    When method POST
    Then status 200
    And match response.id != null
    And match response.remainingAttempts == 8
    And match response.status == "IN_PROGRESS"
    And assert response.maskedWord.length >= 19  /* length is (2n - 1) of the expectation since, we add spaces to mask characters and last character has no space at the end*/
    And assert response.maskedWord.length <= 25


  Scenario: Create new game WITH username - EASY
    Given url baseUrl + '/game'
    And request { "username": "karate", "difficulty": "EASY" }
    When method POST
    Then status 200
    And match response.id != null
    And match response.remainingAttempts == 6
    And match response.status == "IN_PROGRESS"
    And assert response.maskedWord.length >= 7  /* length is (2n - 1) of the expectation since, we add spaces to mask characters and last character has no space at the end*/
    And assert response.maskedWord.length <= 11

  Scenario: Create new game WITH username - NORMAL
    Given url baseUrl + '/game'
    And request { "username": "karate", "difficulty": "NORMAL" }
    When method POST
    Then status 200
    And match response.id != null
    And match response.remainingAttempts == 7
    And match response.status == "IN_PROGRESS"
    And assert response.maskedWord.length >= 13  /* length is (2n - 1) of the expectation since, we add spaces to mask characters and last character has no space at the end*/
    And assert response.maskedWord.length <= 17

  Scenario: Create new game WITH username - HARD
    Given url baseUrl + '/game'
    And request { "username": "karate", "difficulty": "HARD" }
    When method POST
    Then status 200
    And match response.id != null
    And match response.remainingAttempts == 8
    And match response.status == "IN_PROGRESS"
    And assert response.maskedWord.length >= 19  /* length is (2n - 1) of the expectation since, we add spaces to mask characters and last character has no space at the end*/
    And assert response.maskedWord.length <= 25