package ru.yandex.practicum.filmorate.dao.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    Optional<Genre> loadGenreById(int id);

    List<Genre> loadGenresByFilmId(int id);

    void saveGenresInFilm(int id, List<Genre> genres);

    List<Genre> findByIds(List<Integer> ids);

    void deleteGenresInFilm(int id);

    List<Genre> getAllGenres();
}
