package com.example.nts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Button mapping params")
public class ButtonMappingRequest {
    @Schema(description = "ProFlex4 UI button ID", example = "buttonCorrect")
    @NotNull(message = "Cannot be NULL")
    private String buttonID;

    @Schema(description = "Allowed: kz, ru, en", example = "ru")
    @NotNull(message = "Cannot be NULL")
    private String language;
}
