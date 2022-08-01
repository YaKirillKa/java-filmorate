package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    @Min(1)
    Long id;
    @Email
    String email;
    @NotBlank
    String login;
    String name;
    @Past
    LocalDate birthday;
}
