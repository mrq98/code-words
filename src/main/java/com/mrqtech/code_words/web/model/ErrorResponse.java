package com.mrqtech.code_words.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Standard error response format")
public class ErrorResponse {

    @Schema(description = "Short title summarizing the error", example = "Validation Failed")
    private String title;

    @Schema(description = "Detailed error message", example = "The username field is required.")
    private String message;
}