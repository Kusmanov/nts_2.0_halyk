package com.example.nts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Screen mapping params")
public class ScreenMappingRequest {
    @Schema(description = "ProFlex4 UI screen ID", example = "PinEntry")
    @NotNull(message = "Cannot be NULL")
    private String screenID;

    @Schema(description = "Allowed: kz, ru, en", example = "ru")
    @NotNull(message = "Cannot be NULL")
    private String language;
}
