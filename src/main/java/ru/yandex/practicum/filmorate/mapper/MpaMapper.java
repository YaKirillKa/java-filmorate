package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;


@Mapper
public interface MpaMapper {

    Mpa mapToMpa(MpaDto dto);

}
