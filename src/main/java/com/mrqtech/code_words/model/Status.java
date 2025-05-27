package com.mrqtech.code_words.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The current status of the game or player progress",
        example = "IN_PROGRESS",
        allowableValues = {"IN_PROGRESS", "WON", "LOST"})
public enum Status {
    IN_PROGRESS,
    WON,
    LOST
}