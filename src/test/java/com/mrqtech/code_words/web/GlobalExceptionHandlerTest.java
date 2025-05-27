package com.mrqtech.code_words.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrqtech.code_words.exception.GameAlreadyFinishedException;
import com.mrqtech.code_words.exception.InvalidGameException;
import com.mrqtech.code_words.web.model.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {TestController.class})
@Import({GlobalExceptionHandler.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void handleNotFound_Returns404_WithErrorResponse() throws Exception {
        // Arrange
        String expectedTitle = "Entity Not Found";
        String expectedMessage = "Game not found";

        // Act
        ErrorResponse errorResponse = callEndpoint("/test/not-found", status().isNotFound());

        // Assert
        assertResponse(expectedTitle,expectedMessage,errorResponse );
    }

    @Test
    void handleGameAlreadyFinished_Returns400_WithErrorResponse() throws Exception {
        // Arrange
        String expectedTitle = "Game Already Finished";
        String expectedMessage = "Game is already finished";

        // Act
        ErrorResponse errorResponse = callEndpoint("/test/game-finished", status().isBadRequest());

        // Assert
        assertResponse(expectedTitle,expectedMessage,errorResponse );
    }

    @Test
    void handleInvalidGame_Returns400_WithErrorResponse() throws Exception {
        // Arrange
        String expectedTitle = "Invalid Game";
        String expectedMessage = "Can't access game.";

        // Act
        ErrorResponse errorResponse = callEndpoint("/test/invalid-game", status().isBadRequest());

        // Assert
        assertResponse(expectedTitle,expectedMessage,errorResponse );
    }

    @Test
    void handleGeneric_Returns500_WithErrorResponse() throws Exception {
        // Arrange
        String expectedTitle = "Server Error";
        String expectedMessage = "Unexpected error";
        // Act
        ErrorResponse errorResponse = callEndpoint("/test/generic", status().isInternalServerError());

        // Assert
        assertResponse(expectedTitle,expectedMessage,errorResponse );
    }


    private ErrorResponse callEndpoint(String url, ResultMatcher statusExpectation) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(statusExpectation)
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
    }
    private void assertResponse(String expectedTitle, String expectedMessage, ErrorResponse response)  {
        assertEquals(expectedTitle, response.getTitle());
        assertEquals(expectedMessage, response.getMessage());
    }

}

// Mock controller to throw exceptions for testing
@RestController
class TestController {

    @GetMapping("/test/not-found")
    public void throwNotFound() {
        throw new EntityNotFoundException("Game not found");
    }

    @GetMapping("/test/game-finished")
    public void throwGameAlreadyFinished() {
        throw new GameAlreadyFinishedException("Game is already finished");
    }

    @GetMapping("/test/generic")
    public void throwGeneric() {
        throw new RuntimeException("Unexpected error");
    }

    @GetMapping("/test/invalid-game")
    public void throwInvalidGameException() {
        throw new InvalidGameException("Can't access game.");
    }
}