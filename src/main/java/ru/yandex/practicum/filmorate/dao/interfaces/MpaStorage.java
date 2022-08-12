package ru.yandex.practicum.filmorate.dao.interfaces;


import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {
    Optional<Mpa> loadMpaById(int id);

    List<Mpa> loadAllMpa();

}