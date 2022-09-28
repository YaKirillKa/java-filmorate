package ru.yandex.practicum.filmorate.controller;

import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;
    private final ConversionService conversionService;

    public MpaController(MpaService mpaService, ConversionService conversionService) {
        this.mpaService = mpaService;
        this.conversionService = conversionService;
    }

    @GetMapping
    public List<MpaDto> findAll() {
        return mpaService.findAll().stream()
                .map(mpa -> conversionService.convert(mpa, MpaDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MpaDto findById(@PathVariable Long id) {
        Mpa mpa = mpaService.findById(id);
        return conversionService.convert(mpa, MpaDto.class);
    }

}
