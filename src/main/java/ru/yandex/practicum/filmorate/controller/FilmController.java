package ru.yandex.practicum.filmorate.controller;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final ConversionService conversionService;
    private final FilmMapper filmMapper;

    public FilmController(FilmService filmService, ConversionService conversionService, FilmMapper filmMapper) {
        this.filmService = filmService;
        this.conversionService = conversionService;
        this.filmMapper = filmMapper;
    }

    @GetMapping
    public List<FilmDto> findAll() {
        return filmService.findAll().stream()
                .map(film -> conversionService.convert(film, FilmDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public FilmDto findById(@PathVariable Long id) {
        Film film = filmService.findById(id);
        return conversionService.convert(film, FilmDto.class);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopular(@RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.getPopular(count).stream()
                .map(film -> conversionService.convert(film, FilmDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/common")
    public List<FilmDto> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        return filmService.getCommonFilms(userId, friendId).stream()
                .map(film -> conversionService.convert(film, FilmDto.class))
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public FilmDto create(@Valid @NotNull @RequestBody FilmDto filmDto) {
        Film film = filmMapper.mapToFilm(filmDto);
        film = filmService.create(film);
        return conversionService.convert(film, FilmDto.class);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public FilmDto update(@Valid @NotNull @RequestBody FilmDto filmDto) {
        Film film = filmMapper.mapToFilm(filmDto);
        film = filmService.update(film.getId(), film);
        return conversionService.convert(film, FilmDto.class);
    }

}
