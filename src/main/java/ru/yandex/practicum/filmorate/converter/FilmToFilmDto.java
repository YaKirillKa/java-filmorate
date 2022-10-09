package ru.yandex.practicum.filmorate.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class FilmToFilmDto implements Converter<Film, FilmDto> {

    @Override
    public FilmDto convert(Film film) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setDuration(film.getDuration());
        filmDto.setReleaseDate(film.getReleaseDate());
        Mpa mpa = film.getMpa();
        if (mpa != null) {
            MpaDto mpaDto = new MpaDto();
            mpaDto.setId(mpa.getId());
            mpaDto.setName(mpa.getName());
            filmDto.setMpaDto(mpaDto);
        }

        List<GenreDto> genres = new ArrayList<>();
        final Set<Genre> filmGenres = film.getGenres();
        if (filmGenres != null && !filmGenres.isEmpty()) {
            for (Genre g : filmGenres) {
                GenreDto genreDto = new GenreDto();
                genreDto.setId(g.getId());
                genreDto.setName(g.getName());
                genres.add(genreDto);
            }
        }
        filmDto.setGenres(genres);

        List<DirectorDto> directors = new ArrayList<>();
        final Set<Director> filmDirectors = film.getDirectors();
        if (filmDirectors != null && !filmDirectors.isEmpty()) {
            for (Director director : filmDirectors) {
                DirectorDto directorDto = new DirectorDto();
                directorDto.setId(director.getId());
                directorDto.setName(director.getName());
                directors.add(directorDto);
            }
        }
        filmDto.setDirectors(directors);

        return filmDto;
    }
}

