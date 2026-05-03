package org.psi.psinema.domain.movie.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.psi.psinema.domain.movie.Genre;

import java.time.LocalDate;

@Data
public class MovieRequest {
    @NotBlank
    private String title;
    private String description;
    private Genre genre;
    @Min(1)
    private int durationMinutes;
    private String posterUrl;
    private String director;
    private LocalDate releaseDate;
}
