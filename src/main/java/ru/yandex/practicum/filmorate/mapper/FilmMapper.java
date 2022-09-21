package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;


@Mapper(uses = MpaMapper.class)
public interface FilmMapper {

    @Mapping(source = "mpaDto", target = "mpa")
    Film mapToFilm(FilmDto dto);

}
