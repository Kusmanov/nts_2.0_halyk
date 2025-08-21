package com.example.nts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Playback params")
public class PlaybackRequest {
    @Schema(description = "Array of file names. Numbers must be in the range 0 - 999 999 999", example = """
            ["003_0_PinEntry_rus.wav", "003_1_PinEntry_rus.wav", "003_2_PinEntry_rus.wav"]""")
    @NotNull(message = "Cannot be NULL")
    private List<String> audioFiles;

    @Schema(description = "Allowed: kz, ru, en", example = "ru")
    @NotNull(message = "Cannot be NULL")
    private String language;
}
