package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.mpa.MpaDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
public class MpaService {

    public static final String MPA_NOT_FOUND = "MPA %s not found";
    private final MpaDao mapDao;

    public MpaService(MpaDao mapDao) {
        this.mapDao = mapDao;
    }

    public List<Mpa> findAll() {
        return mapDao.findAll();
    }

    public Mpa findById(Long id) {
        return mapDao.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(MPA_NOT_FOUND, id)));
    }
}
