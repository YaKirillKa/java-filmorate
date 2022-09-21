package ru.yandex.practicum.filmorate.controller;

import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;
    private final ConversionService conversionService;

    public GenreController(GenreService genreService, ConversionService conversionService) {
        this.genreService = genreService;
        this.conversionService = conversionService;
    }

    @GetMapping
    public List<GenreDto> findAll() {
        return genreService.findAll().stream()
                .map(genre -> conversionService.convert(genre, GenreDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public GenreDto findById(@PathVariable Long id) {
        Genre genre = genreService.findById(id);
        return conversionService.convert(genre, GenreDto.class);
    }

}
