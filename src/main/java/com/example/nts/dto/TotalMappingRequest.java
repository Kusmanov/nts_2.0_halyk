package com.example.nts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Total mapping params")
public class TotalMappingRequest {
    @Schema(description = "File name", example = "total.txt")
    @NotNull(message = "Cannot be NULL")
    private String filename;

    @Schema(description = "Allowed: kz, ru, en", example = "ru")
    @NotNull(message = "Cannot be NULL")
    private String language;
}
