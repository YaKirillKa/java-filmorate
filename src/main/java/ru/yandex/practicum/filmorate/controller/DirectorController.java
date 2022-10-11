package ru.yandex.practicum.filmorate.controller;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;
    private final DirectorMapper directorMapper;
    private final ConversionService conversionService;

    public DirectorController(DirectorService directorService,
                              DirectorMapper directorMapper, ConversionService conversionService) {
        this.directorService = directorService;
        this.directorMapper = directorMapper;
        this.conversionService = conversionService;
    }

    @GetMapping
    public List<DirectorDto> findAll() {
        return directorService.findAll().stream()
                .map(director -> conversionService.convert(director, DirectorDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public DirectorDto findById(@PathVariable Long id) {
        Director director = directorService.findById(id);
        return conversionService.convert(director, DirectorDto.class);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public DirectorDto create(@Valid @NotNull @RequestBody DirectorDto directorDto) {
        Director director = directorMapper.mapToDirector(directorDto);
        director = directorService.create(director);
        return conversionService.convert(director, DirectorDto.class);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public DirectorDto update(@Valid @NotNull @RequestBody DirectorDto directorDto) {
        Director director = directorMapper.mapToDirector(directorDto);
        director = directorService.update(director);
        return conversionService.convert(director, DirectorDto.class);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        directorService.deleteById(id);
    }
}
