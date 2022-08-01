package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.constraints.AfterDate;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    @Min(1)
    Long id;
    @NotBlank
    String name;
    @Size(max = 200)
    String description;
    @AfterDate(date = "1895-12-28")
    String releaseDate;
    @Positive
    int duration;
}
