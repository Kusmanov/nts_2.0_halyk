package com.example.nts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Balance mapping params")
public class BalanceMappingRequest {
    @Schema(description = "File name", example = "balance.txt")
    @NotNull(message = "Cannot be NULL")
    private String filename;

    @Schema(description = "Allowed: kz, ru, en", example = "ru")
    @NotNull(message = "Cannot be NULL")
    private String language;
}
